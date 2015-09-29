package de.btu.openinfra.backend.db.rbac;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;

import de.btu.openinfra.backend.db.daos.rbac.SubjectDao;
import de.btu.openinfra.backend.db.jpa.model.rbac.RolePermission;
import de.btu.openinfra.backend.db.jpa.model.rbac.Subject;
import de.btu.openinfra.backend.db.jpa.model.rbac.SubjectRole;

/**
 * This is the OpenInfraRealm which is used to retrieve user-specific 
 * information from database. The idea behind this implementation was: 
 * 'keep it stupid and simple' and it's closely related to the implementation
 * of the origin jdbcRealm.
 * 
 * @author <a href="http://www.b-tu.de">BTU</a> DBIS
 *
 */
public class OpenInfraRealm extends AuthorizingRealm {
	
	@Override
	protected SaltedAuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		
		UsernamePasswordToken upt = (UsernamePasswordToken)token;
		Subject s = new SubjectDao().readModel(upt.getUsername());
		
		return new SimpleAuthenticationInfo(
				new SimplePrincipalCollection(
						upt.getUsername(),
						OpenInfraRealmNames.LOGIN.name()), 
						s.getPassword(),
						new SimpleByteSource(s.getSalt().toString()));
	}
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		
		// Retrieve the login from principals (there might be multiple)
		List<String> login = new LinkedList<String>();
		for(Object o : principals.fromRealm(OpenInfraRealmNames.LOGIN.name())) {
			login.add(o.toString());
		}
		
		// Currently, we assume that there is only one entry in the principal 
		// collection with the name: 'OpenInfraRealmNames.LOGIN.name()'. This
		// login name is defined in the 'doGetAuthenticationInfo' method above.
		// Thus, we use always the very first entry. However, this might change
		// in the future and should be changed.
        Subject s = new SubjectDao().readModel(login.get(0));
        
        Set<String> roleNames = new HashSet<String>();
        Set<String> permissions = new HashSet<String>();
        
        for(SubjectRole sr : s.getSubjectRoles()) {
        	roleNames.add(sr.getRoleBean().getName());
        	for(RolePermission rp : sr.getRoleBean().getRolePermissions()) {
        		permissions.add(rp.getPermissionBean().getPermission());
        	}
        }
        		
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
        info.setStringPermissions(permissions);
        return info;
	}
	
	/**
	 * Creates an encrypted password.
	 * <a href="http://shiro.apache.org/realm.html"/>
	 * @param password the password to encrypt
	 * @return the encrypted password
	 */
	public static String encrypt(String plainTextPassword, UUID salt) {
		//Hash the plain-text password with the salt and multiple
		//iterations and then Base64-encode the value (requires less 
		//space than Hex):
		// TODO: the value 1024 is defined in the shiro.ini and should match
		// the same value 'credentialsMatcher.hashIterations = 1024'
		return new Sha256Hash(
				plainTextPassword, 
				salt.toString(), 
				1024).toBase64();		
	}

}
