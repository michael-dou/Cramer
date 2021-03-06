package com.cw.cramer.core.security;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import com.cw.cramer.auth.service.SysUserService;

/**
 * 安全身份管理类
 * @author wicks
 */
public class ShiroRealm extends AuthorizingRealm{
	
	@Resource
	private SysUserService sysUserService;

	/**
	 * 获得权限
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		authorizationInfo.addRole("admin");
		authorizationInfo.addStringPermission("admin:manger");
		return authorizationInfo;
	}

	/**
	 * 验证登录
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken)authcToken;  
		
//		String username = (String)token.getPrincipal();
//		User user = userService.findByUsername(username);
//		if(user == null) {
//		throw new UnknownAccountException();//没找到帐号
//		}
//		if(Boolean.TRUE.equals(user.getLocked())) {
//		throw new LockedAccountException(); //帐号锁定
//		}
//		//交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家
//		的不好可以在此判断或自定义实现
//		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
//		user.getUsername(), //用户名
//		user.getPassword(), //密码
//		ByteSource.Util.bytes(user.getCredentialsSalt()),//salt=username+salt
//		getName() //realm name
//		);
//		return authenticationInfo;
        
        if(sysUserService.checkPassWord(token.getUsername(), String.valueOf(token.getPassword())) == 1){  
            AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(token.getUsername(), token.getPassword(), this.getName());  
            this.setSession("currentUser", token.getUsername());  
            return authcInfo;  
        }

		return null;
	}
	
    /** 
     * 将一些数据放到ShiroSession中,以便于其它地方使用 
     * @see 比如Controller,使用时直接用HttpSession.getAttribute(key)就可以取到 
     */  
    private void setSession(Object key, Object value){  
        Subject currentUser = SecurityUtils.getSubject();  
        if(null != currentUser){  
            Session session = currentUser.getSession();  
            if(null != session){  
                session.setAttribute(key, value);  
            }  
        }  
    } 

}
