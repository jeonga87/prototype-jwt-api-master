package kr.co.demo.core.domain;

import kr.co.demo.common.Base;
import kr.co.demo.common.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Class Name : User.java
 * Description : 사용자 domain
 * Writer : lee.j
 */

@Alias("user")
@Data
@EqualsAndHashCode(callSuper=false)
public class User extends Base implements UserDetails, Serializable {

	/** 사용자 일련번호*/
	private Long idx;

	/** 권한 */
	private String userRole;

	/** 로그인 계정 아이디 */
	private String cspCode;

	/** 코드 */
	private String code;

	/** 지사 */
	private String branch;

	/** 센터 */
	private String center;

	/** 이름 */
	private String name;

	/** 생성자 */
	private String createdBy;

	/** 등록일 */
	private Date createdDt;

	/** 수정자 */
	private String modifiedBy;

	/** 수정일 */
	private Date modifiedDt;

	/** 아이피 */
	private String ip;

	/** 코드 */
	private String appKey;

	private Set<Role> authorities = new HashSet();

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		authorities.add(new Role(userRole));
		return authorities;
	}

	@Override
	public String getUsername() {
		return cspCode;
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
}
