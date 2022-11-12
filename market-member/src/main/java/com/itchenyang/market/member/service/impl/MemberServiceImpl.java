package com.itchenyang.market.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;
import com.itchenyang.market.member.dao.MemberDao;
import com.itchenyang.market.member.entity.MemberEntity;
import com.itchenyang.market.member.entity.MemberLevelEntity;
import com.itchenyang.market.member.exception.PhoneExistException;
import com.itchenyang.market.member.exception.UserNameExistException;
import com.itchenyang.market.member.service.MemberService;
import com.itchenyang.market.member.vo.UserLoginVo;
import com.itchenyang.market.member.vo.UserRegisterVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(UserRegisterVo vo) {
        MemberEntity entity = new MemberEntity();
        // 查询默认会员等级
        MemberLevelEntity levelEntity = baseMapper.getDefaultLevel();
        entity.setLevelId(levelEntity.getId());

        // 设置用户名、电话，校验是否唯一
        checkUserNameExist(vo.getUserName());
        checkPhoneExist(vo.getPhone());
        entity.setUsername(vo.getUserName());
        entity.setPassword(vo.getPhone());

        // 设置密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);

        baseMapper.insert(entity);
    }

    @Override
    public Boolean login(UserLoginVo vo) {
        String key = vo.getLoginacct();
        String password = vo.getPassword();

        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", key).or().eq("mobile", key));
        if (entity == null) {
            return false;
        } else {
            String savePassword = entity.getPassword();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.matches(password, savePassword);
        }
    }

    private void checkUserNameExist(String userName) {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (count > 0) {
            throw new UserNameExistException();
        }
    }

    private void checkPhoneExist(String phone) {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0) {
            throw new PhoneExistException();
        }
    }

}