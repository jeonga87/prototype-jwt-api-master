package kr.co.demo.controller;

import kr.co.demo.core.domain.User;
import kr.co.demo.core.domain.JwtResponse;
import kr.co.demo.core.service.UserService;
import kr.co.demo.security.JwtTokenUtil;
import kr.co.demo.util.ApiResponseMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Class Name : UserController.java
 * Description : 사용자
 * Writer : lee.j
 */

@RestController
public class UserController extends BaseFormController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  RedisTemplate<String, Object> redisTemplate;

  @Value("${demo.appKey}")
  private String appKey;

  //redis 만료시간 7일
  public static final long JWT_REDIS_VALIDITY = 1000 * 6 * 10 * 60 * 24 * 7;

   /**
   * user 인증
   * @param userRequest
   * @return
   */
  @PostMapping("/user/login")
  public ResponseEntity<?> auth(@RequestBody User userRequest) throws Exception {

    final UserDetails userDetails = userService.loadUserByUsername(userRequest.getCspCode());

    //cspCode 없음
    if (userDetails == null) {
      return new ResponseEntity(new ApiResponseMessage("601", "csp code fail"), HttpStatus.BAD_REQUEST);
    }

    //appkey, code 없음/틀림
    if (StringUtils.isBlank(userRequest.getCode())) {           //mobile 로그인 - appkey 확인
      if (StringUtils.isBlank(userRequest.getAppKey()) || !appKey.equals(userRequest.getAppKey())) {
        return new ResponseEntity(new ApiResponseMessage("602", "key fail"), HttpStatus.BAD_REQUEST);
      }
    } else if (StringUtils.isBlank(userRequest.getAppKey())) {  //pc 로그인 - code 확인
      if (StringUtils.isBlank(userRequest.getCode()) || !(userDetails.getPassword()).equals(userRequest.getCode())) {
        return new ResponseEntity(new ApiResponseMessage("602", "key fail"), HttpStatus.BAD_REQUEST);
      }
    } else {
      return new ResponseEntity(new ApiResponseMessage("602", "key fail"), HttpStatus.BAD_REQUEST);
    }

    final String token = jwtTokenUtil.generateToken(userDetails);

    redisTemplate.opsForValue().set(userRequest.getCspCode(), token);
    redisTemplate.expire(userRequest.getCspCode(), JWT_REDIS_VALIDITY, TimeUnit.MILLISECONDS);

    return new ResponseEntity(new JwtResponse(token), HttpStatus.OK);
  }

  private boolean authenticate(User user) throws Exception {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getCspCode(), user.getPassword()));
    } catch (Exception e) {
      log.debug("authenticate error : " + user.getCspCode());
      return false;
    }
    return true;
  }

   /**
   * user 정보
   * @return
   */
  @GetMapping("/user/info")
  public ResponseEntity<?> info(HttpServletRequest request) {

    String cspCode = jwtTokenUtil.getCurrentUser(request);

    if(cspCode != null) {
      User user = userService.getUser(cspCode);

      Map<String, Object> resultMap = new HashMap<>();
      resultMap.put("branch", user.getBranch());
      resultMap.put("center", user.getCenter());
      resultMap.put("cspCode", user.getCspCode());
      resultMap.put("name", user.getName());
      resultMap.put("role", user.getAuthorities().stream().map(r-> r.getAuthority()).collect(Collectors.toSet()));

      String redisToken =  (String)redisTemplate.opsForValue().get(cspCode);

      if (resultMap.get("cspCode") != null && redisToken != null) {
        return new ResponseEntity(resultMap, HttpStatus.OK);
      }
    }

    return new ResponseEntity(new ApiResponseMessage("603", "user info fail"), HttpStatus.BAD_REQUEST); //정보 없음
  }

  /**
   * user 로그아웃
   * @return
   */
  @GetMapping("/user/logout")
  public ResponseEntity logout(HttpServletRequest request) {

    String cspCode = jwtTokenUtil.getCurrentUser(request);

    if(cspCode != null) {
      if (redisTemplate.opsForValue().get(cspCode) != null) {
        redisTemplate.delete(cspCode);
        return new ResponseEntity(HttpStatus.OK);
      }
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  /**
   * user 코드 생성
   * @return
   */
  @GetMapping("/user/code")
  public ResponseEntity createCode(HttpServletRequest request) {

    String cspCode = jwtTokenUtil.getCurrentUser(request);
    if (cspCode != null) {
      return ResponseEntity.ok(userService.updateUserCode(cspCode));
    }

    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }
}
