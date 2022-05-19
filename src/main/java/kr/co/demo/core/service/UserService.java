package kr.co.demo.core.service;

import kr.co.demo.core.domain.User;
import kr.co.demo.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static kr.co.demo.common.Function.getRandomStr;

/**
 * Class Name : UserService.java
 * Description : 사용자 service
 * Writer : lee.j
 */

@Service
public class UserService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  /**
   * 사용자 등록
   * @param user
   * @return int
   */
  @Transactional
  public int insertUser(User user) throws Exception {
    //암호화
    return userRepository.insertUser(user);
  }

  /**
   * 사용자 조회
   *
   * @return 조건에 해당하는 사용자
   */
  @Transactional(readOnly = true)
  public User getUser(String cspCode) {
    return userRepository.selectUser(cspCode);
  }

  @Override
  public UserDetails loadUserByUsername(String cspCode) {
    User user = userRepository.selectUser(cspCode);
    if (user == null) {
      return null;
    }
    return new org.springframework.security.core.userdetails.User(user.getCspCode(), user.getCode() == null ? "" : user.getCode(), new ArrayList<>());
  }

  /**
   * 사용자 로그 등록
   * @param user
   * @return int
   */
  @Transactional
  public int insertUserLog(User user) {
    return userRepository.insertUserLog(user);
  }

  /**
   * 사용자 코드 생성
   * @param cspCode
   * @return int
   */
  @Transactional
  public Map<String, String> updateUserCode(String cspCode) {
    String random = getRandomStr(5);

    User user = new User();
    user.setCspCode(cspCode);
    user.setCode(random);
    userRepository.updateUserCode(user);

    Map<String, String> resultMap = new HashMap<>();
    resultMap.put("code", random);

    return resultMap;
  }

}
