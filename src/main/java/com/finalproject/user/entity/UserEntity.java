package com.finalproject.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 *
 * @author TB
 * @email tb17074107@gmail.com
 * @date 2022-12-01 20:51:55
 */
@Data
@TableName("user")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 *
	 */
	private String username;
	/**
	 *
	 */
	private String password;
	/**
	 *
	 */
	private String nickname;
	/**
	 *
	 */
	private String email;
	/**
	 *
	 */
	private String telephone;
	/**
	 *
	 */
	private String status;
	/**
	 *
	 */
	private String avatar;
	/**
	 *
	 */
	private Date createTime;
	/**
	 *
	 */
	private Date lastLogin;
	/**
	 *
	 */
	private String socialUuid;

}
