package kr.co.demo.core.repository;

import kr.co.demo.core.domain.User;
import org.springframework.stereotype.Repository;

/**
 * Class Name : UserRepository.java
 * Description : 사용자 repository
 * Writer : lee.j
 */

@Repository
public interface UserRepository {

	 /**
	 * 사용자 등록
	 * @param user
	 * @return int
	 */
	int insertUser(User user) throws Exception;

	/**
	 * 사용자 조회
	 * @param cspCode
	 * @return 조건에 해당하는 사용자
	 */
	User selectUser(String cspCode);

	/**
	 * 사용자 로그 등록
	 * @param user
	 * @return int
	 */
	int updateUserCode(User user);

	/**
	 * 사용자 로그 등록
	 * @param user
	 * @return int
	 */
	int insertUserLog(User user);
}
