package kr.co.demo.core.domain;

import java.io.Serializable;

/**
 * Class Name : JwtResponse.java
 * Description : 토큰 domain
 * Writer : lee.j
 */

public class JwtResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwttoken;

	public JwtResponse(String jwttoken) {
		this.jwttoken = jwttoken;
	}

	public String getToken() {
		return this.jwttoken;
	}
}