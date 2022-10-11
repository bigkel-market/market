package com.itchenyang.market.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.market.product.dao.CategoryBrandRelationDao;
import com.itchenyang.market.product.dao.CategoryDao;
import com.itchenyang.market.product.entity.CategoryEntity;
import com.itchenyang.market.product.entity.Catelog2Vo;
import com.itchenyang.market.product.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public List<CategoryEntity> selectByTree() {
        List<CategoryEntity> all = baseMapper.selectList(null);
        // 找出第一层级
        List<CategoryEntity> treeList = all.stream()
                .filter(one -> one.getParentCid() == 0)
                .map(one -> {
                    one.setChildren(getChildren(one, all));
                    return one;
                })    // 设置children
                .sorted((o1, o2) -> {
                    return (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 : o2.getSort());
                })
                .collect(Collectors.toList());
        return treeList;
    }

    @Override
    public void removeMenuByIds(List<Long> ids) {
        // todo 判断是否允许被删除

        baseMapper.deleteBatchIds(ids);
    }

    @Override
    public List<Long> findCatelogPath(Long catelogId) {
        List<Long> catePath = new ArrayList<>();
        findParentPath(catelogId, catePath);

        // 倒序
        Collections.reverse(catePath);
        return catePath;
    }

//    失效模式
//    @CacheEvict(value = {"category"}, key = "'getLevel1Categorys'")

//    批量失效
//    @Caching(evict = {
//            @CacheEvict(value = {"category"}, key = "'getLevel1Categorys'"),
//            @CacheEvict(value = {"category"}, key = "'getCatelogJson'")
//    })
    @CacheEvict(value = {"category"}, allEntries = true)
    @Transactional
    @Override
    public void updateCasda(CategoryEntity category) {
        baseMapper.updateById(category);
        // 级联更新
        if (!StringUtils.isBlank(category.getName())) {
            categoryBrandRelationDao.updateCasdaCategory(category.getCatId(), category.getName());
        }
    }

    // value 缓存分区名字  key 是健 SpEL表达式 key = "'String'"
    // sync 加本地锁，解决缓存击穿
    @Cacheable(value = {"category"}, key = "#root.methodName", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    // TODO 堆外内存溢出 OutOfDirectMemoryError
    // 使用lettuce作为操作redis的客户端，使用netty进行网络通信
    // netty没有指定堆外内存，默认使用-Xmx指定的JVM内存大小
    // 可以通过-Dio.netty.maxDirectMemory进行设置

    // 解决方案：不能只使用-Dio.netty.maxDirectMemory调大堆外内存
    // 1、升级lettuce   2、切换使用jedis
    @Cacheable(value = {"category"}, key = "#root.methodName", sync = true)
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbRedissonLock();
        return catalogJsonFromDb;
    }

    /**
     * Redis分布式锁
     * 所有线程都去抢占一个占位符，抢占成功后执行业务并缓存数据，然后删掉占位符。未成功的则自旋去抢占占位符
     * set lock UUID NX EX 300 s
     * 注意：
     *    1、设置占位符时，需要设置过期时间，并且两个操作需要保证原子性
     *    2、删除占位符时，只能够删除掉自己设置的占位符，线程2的lock被1删除，2的业务没执行完，3又占位成功，产生多个线程同时执行业务
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbRedisLock() {
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock != null && lock) {
            Map<String, List<Catelog2Vo>> map;
            try {
                map = getCatalogJsonFromDb();
            }finally {
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            return map;
        } else {
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }
            return getCatalogJsonFromDbRedisLock();
        }
    }

    /**
     * Redisson 实现分布式锁
     * 默认加锁30秒，看门狗  若业务执行耗时超过 看门狗 / 3  10s的时间，则自动续期，若没有业务执行，则到期自动释放锁
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbRedissonLock() {
        RLock lock = redissonClient.getLock("categoryJson_lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> map = new HashMap<>();
        try {
            map = getCatalogJsonFromDb();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return map;
    }


    /**
     * synchronized(this)  SpringBoot容器中对象是单例的，那么一个this就可以锁住所有线程
     * 线程进了锁之后 再次确认缓存是否有数据
     * @return
     */
    public synchronized Map<String, List<Catelog2Vo>> getCatalogJsonFromDbLocalLock() {
        return getCatalogJsonFromDb();
    }

    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {

        // 首先查询出所有内容
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        // 查出所有一级分类
        List<CategoryEntity> level1Categorys = getCatalogByParentCid(categoryEntities, 0L);

        // 封装返回的数据
        Map<String, List<Catelog2Vo>> map = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 查询二级节点
            List<CategoryEntity> level2Categorys = getCatalogByParentCid(categoryEntities, v.getCatId());
            List<Catelog2Vo> level2 = new ArrayList<>();
            if (level2Categorys != null) {
                level2 = level2Categorys.stream().map(l2 -> {
                    // 父级catId, 子级列表, 本级catId, 本级name
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    List<CategoryEntity> level3Categorys = getCatalogByParentCid(categoryEntities, l2.getCatId());
                    if (level3Categorys != null) {
                        List<Catelog2Vo.Catelog3Vo> level3 = level3Categorys.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(level3);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return level2;
        }));
        return map;
    }


    private List<CategoryEntity> getCatalogByParentCid(List<CategoryEntity> categoryEntities, Long parentCid) {
        List<CategoryEntity> collect = categoryEntities.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return collect;
    }

    public void findParentPath(Long id, List<Long> catePath) {
        catePath.add(id);
        CategoryEntity current = baseMapper.selectById(id);
        if (current.getParentCid() != 0) {
            findParentPath(current.getParentCid(), catePath);
        }
    }

    public List<CategoryEntity> getChildren(CategoryEntity cur, List<CategoryEntity> all) {
        List<CategoryEntity> treeList = all.stream()
                .filter(one -> one.getParentCid().equals(cur.getCatId()))
                .map(one -> {
                    one.setChildren(getChildren(one, all));
                    return one;
                })    // 设置children
                .sorted((o1, o2) -> {
                    return (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 : o2.getSort());
                })
                .collect(Collectors.toList());
        return treeList;
    }
}