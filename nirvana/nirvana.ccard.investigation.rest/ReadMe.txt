Camel Router WAR Project with Web Console and REST Support
==========================================================

This project bundles the Camel Web Console, REST API, and some
sample routes as a WAR. You can build the WAR by running

mvn install

You can then run the project by dropping the WAR into your 
favorite web container or just run

mvn jetty:run

to start up and deploy to Jetty.


Web Console
===========

You can view the Web Console by pointing your browser to http://localhost:8080/

You should be able to do things like

    * browse the available endpoints
    * browse the messages on an endpoint if it is a BrowsableEndpoint
    * send a message to an endpoint
    * create new endpoints

For more help see the Apache Camel documentation

    http://camel.apache.org/

征信接口说明:
征信主页:https://ipcrs.pbccrc.org.cn/

接口:http://192.168.1.76:18088/credit/zxPublic.go
参数:
	sign:校验key 8A5880E44DB942F291F03D6A47511889 (必须)
	cuserId:用户id,会话过程以此为标识 (必须)
	from:来源 1:公积金
	method:具体业务方法 (必须)

		以下是method取不同值时，具体业务需要的参数

		zxVerifyCode:获取征信图片验证码
			附加参数:
				type:0登录;1注册;2重置密码;3找回登录名 (必须)
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxVerifyCode&type=0
			成功返回:验证码图片。
			失败返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="加载验证码图片失败"></Resp>

		zxLogin:征信登录
			附加参数:
				loginname:登录名 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				password:密码 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				(注:加密后会出现"=","/"之类的字符，需URLEncode后传递)
				code:验证码 (必须)
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxLogin&loginname=Sla32NHRfDutY5pZzurEAw%3d%3d&password=3VGiWhqQVdqJCIV6Yca%2beQ%3d%3d&code=hkp433
			成功返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="登录成功"></Resp>
			失败返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="0" desc="验证码输入错误,请重新输入"></Resp>

		zxLoginOut:退出登录
			附加参数:无,根据cuserId结束会话
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxLoginOut

		zxCheckIdentity:征信注册验证身份证是否有效
			附加参数:
				code:验证码 (必须,获取时请使用type=1,注册验证码)
				username:真实姓名 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				idCardNo:身份证号 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				(注:加密后会出现"=","/"之类的字符，需URLEncode后传递)
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxCheckIdentity&code=3u4vpk&username=SWP%2by%2bfu3pU5B1%2bU8yQopw%3d%3d&idCardNo=4E7LpM7lPPisgcStKDAnsRZex0l%2bOxOIhYXszWxyfa8%3d
			成功返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="验证有效身份证成功"></Resp>

		zxCheckRegLoginnameHasUsed:征信验证登录用户名是否可用
			附加参数:
				loginname:登录名 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				(注:加密后会出现"=","/"之类的字符，需URLEncode后传递)
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxCheckRegLoginnameHasUsed&loginname=Sla32NHRfDutY5pZzurEAw%3d%3d
			成功返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="用户名可用"></Resp>
			失败返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="0" desc="用户名已存在"></Resp>

		zxGetAcvitaveCode:征信获取短信验证码
			附加参数:
				mobileTel:手机号 (必须)
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxGetAcvitaveCode&mobileTel=15111111111
			成功返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="认证码已发送"></Resp>
			失败返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="0" desc="认证码发送失败"></Resp>

		zxRegistered:征信注册
			附加参数:
				loginname:登录名 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				password:密码 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				confirmpassword:确认密码 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				mailAddress:邮箱 (必须)
				mobileTel:手机号 (必须)
				code:短信验证码 (必须)
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxRegistered&loginname=no88es&password=22222&confirmpassword=22222&mailAddress=1212.323&mobileTel=15266656654&code=1234
			成功返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="您在个人信用信息平台已注册成功"></Resp>

		zxGoToResetPwd:征信忘记密码认证用户信息
			附加参数:
				loginname:登录名 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				username:真实姓名 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				idCardNo:身份证号 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				(注:加密后会出现"=","/"之类的字符，需URLEncode后传递)
				code:验证码 (必须,获取时请使用type=2,重置验证码)
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxGoToResetPwd&loginname=Sla32NHRfDutY5pZzurEAw%3d%3d&username=SWP%2by%2bfu3pU5B1%2bU8yQopw%3d%3d&idCardNo=4E7LpM7lPPisgcStKDAnsRZex0l%2bOxOIhYXszWxyfa8%3d&code=jk889i
			成功返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="验证用户信息成功"><phone>150*****024</phone></Resp>

		zxGetResetAcvitaveCode:忘记密码短信验证码
			附加参数:无
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxGetResetAcvitaveCode
			成功返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="认证码已发送"></Resp>

		zxGetResetQuestions:重置密码申请问题
			附加参数:
				password:新密码(必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				confirmpassword:新密码确认(必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				code:短信验证码(必须)

			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxGetResetQuestions&password=3VGiWhqQVdqJCIV6Yca%2beQ%3d%3d&confirmpassword=3VGiWhqQVdqJCIV6Yca%2beQ%3d%3d&code=fvrxnv
			返回问题列表:
			<?xml version="1.0" encoding="UTF-8"?>
			<Resp code="1" desc="">
				<question value="2013年01月至2016年06月期间，您办理的所有正在使用的信用卡中，最高授信额度为多少？" quesnum="1" >
				<options value="7001-17000" num="1" />
				<options value="17001-27000" num="2" />
				<options value="27001-37000" num="3" />
				<options value="37001-47000" num="4" />
				<options value="以上都不是" num="5" />
				</question>
				<question value="您曾在以下哪家机构办理过信用卡，并且正在使用？" quesnum="2" >
				<options value="江苏银行,恒丰银行" num="1" />
				<options value="招商银行,中国农业银行" num="2" />
				<options value="平安银行,浙江民泰商业银行" num="3" />
				<options value="北京银行,中国建设银行" num="4" />
				<options value="以上都不是" num="5" />
				</question>
				<question value="2013年01月至2016年06月期间，您的所有正在使用的信用卡中，最近办理的一张信用卡的发卡行为哪家？" quesnum="3" >
				<options value="广发银行" num="1" />
				<options value="招商银行" num="2" />
				<options value="温州银行" num="3" />
				<options value="中国工商银行" num="4" />
				<options value="以上都不是" num="5" />
				</question>
				<question value="您在2013年09月的通讯地址是哪里？" quesnum="4" >
				<options value="上海市上海市徐汇区桂平路３９１号Ａ座５０" num="1" />
				<options value="中国宝鸡市金台区石油家属院" num="2" />
				<options value="河北区民权门义江里9-204" num="3" />
				<options value="富裕县富路镇长兴村１组" num="4" />
				<options value="以上都不是" num="5" />
				</question>
				<question value="您2013年09月的婚姻状况是什么？" quesnum="5" >
				<options value="丧偶" num="1" />
				<options value="未婚" num="2" />
				<options value="已婚" num="3" />
				<options value="离婚" num="4" />
				<options value="以上都不是" num="5" />
				</question>
			</Resp>


		zxApplyResetPwd:重置密码申请提交
			附加参数:
				options:以上问题的答案,按顺序拼接,用小写字母'o'隔开(必须)
			示例：zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxApplyResetPwd
			&options=2o1o2o2o1
			成功返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="您的重置密码申请提交成功，请耐心等待。您可在24小时后登录平台查看验证结果"></Resp>

		zxFindLoginName:找回用户名
			附加参数:
				code:验证码 (必须,获取时请使用type=1,注册验证码)
				username:真实姓名 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				idCardNo:身份证号 (必须,加密com.caiyi.common.security.CaiyiEncrypt.encryptStr)
				(注:加密后会出现"=","/"之类的字符，需URLEncode后传递)
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxFindLoginName&code=3u4vpk&username=SWP%2by%2bfu3pU5B1%2bU8yQopw%3d%3d&idCardNo=4E7LpM7lPPisgcStKDAnsRZex0l%2bOxOIhYXszWxyfa8%3d
			成功返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="您的登录名已短信发送至平台预留的手机号码，请查收。"></Resp>

		zxGetQuestions:征信信用报告问题认证获取
			附加参数：
				iskeep:是否保留已存在的产品信息.0不保留;(可选)
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxGetQuestions

			返回保留提示(出现此提示,如果继续申请,请再次请求此方法,并附加参数iskeep=0)
				<?xml version="1.0" encoding="UTF-8"?><Resp code="2" desc="您的个人信用信息产品已存在。若继续申请查询，现有的个人信用信息产品将不再保留，是否继续？">

			短信验证(用户已开启短信快捷认证,之后请使用zxSpeedApplyReport进行提交)：
			<?xml version="1.0" encoding="UTF-8"?><Resp code="3" desc="短信验证码请求成功"></Resp>

			不支持验证方式:
				<?xml version="1.0" encoding="UTF-8"?><Resp code="0" desc="暂不支持当前验证方式,请到官网申请."></Resp>

			返回问题:
			<?xml version="1.0" encoding="UTF-8"?>
			<Resp code="1" desc="">
				<question value="2013年01月至2016年06月期间，您办理的所有正在使用的信用卡中，最高授信额度为多少？" quesnum="1" >
				<options value="7001-17000" num="1" />
				<options value="17001-27000" num="2" />
				<options value="27001-37000" num="3" />
				<options value="37001-47000" num="4" />
				<options value="以上都不是" num="5" />
				</question>
				<question value="您曾在以下哪家机构办理过信用卡，并且正在使用？" quesnum="2" >
				<options value="江苏银行,恒丰银行" num="1" />
				<options value="招商银行,中国农业银行" num="2" />
				<options value="平安银行,浙江民泰商业银行" num="3" />
				<options value="北京银行,中国建设银行" num="4" />
				<options value="以上都不是" num="5" />
				</question>
				<question value="2013年01月至2016年06月期间，您的所有正在使用的信用卡中，最近办理的一张信用卡的发卡行为哪家？" quesnum="3" >
				<options value="广发银行" num="1" />
				<options value="招商银行" num="2" />
				<options value="温州银行" num="3" />
				<options value="中国工商银行" num="4" />
				<options value="以上都不是" num="5" />
				</question>
				<question value="您在2013年09月的通讯地址是哪里？" quesnum="4" >
				<options value="上海市上海市徐汇区桂平路３９１号Ａ座５０" num="1" />
				<options value="中国宝鸡市金台区石油家属院" num="2" />
				<options value="河北区民权门义江里9-204                                                       " num="3" />
				<options value="富裕县富路镇长兴村１组" num="4" />
				<options value="以上都不是" num="5" />
				</question>
				<question value="您2013年09月的婚姻状况是什么？" quesnum="5" >
				<options value="丧偶" num="1" />
				<options value="未婚" num="2" />
				<options value="已婚" num="3" />
				<options value="离婚" num="4" />
				<options value="以上都不是" num="5" />
				</question>
			</Resp>

		zxApplyReport:征信信用报告提交申请
			附加参数:
				options:以上问题的答案,按顺序拼接,用小写字母'o'隔开(必须)
			示例：zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxApplyReport&options=1o2o2o5o2
			成功返回:
				<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="您的信用信息查询请求已提交，请在24小时后访问平台获取结果，身份验证不通过请重新申请。为保障您的信息安全，您申请的信用信息将于7日后自动清理，请及时获取查询结果。"></Resp>
		zxSpeedApplyReport:征信信用报告短信快捷申请
			附加参数:
				code:短信动态码
				iskeep:是否保留已存在的产品信息.0不保留;(可选)
			示例：zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxSpeedApplyReport&code=uirsqf
			成功返回:
				<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="您于2016年10月08日15时52分15秒提交的个人信用报告查询申请正在受理，请耐心等待。"></Resp>

		zxViewReport:征信获取信用报告
			附加参数:
				code:短信验证码,提交申请后24小时内会收到短信验证码(必须)
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxViewReport&code=23213
			成功返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="获取信用报告成功"></Resp>
			失败返回:<?xml version="1.0" encoding="UTF-8"?><Resp code="0" desc="获取信用报告失败，请稍后重试"></Resp>

		queryUserCreditreFerence:查询征信报告
			附加参数:无
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=queryUserCreditreFerence
			成功返回:
				<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="查询成功"><row icrid="3" cloginname="469412882" cuserid="84d45b82c303" creportno="2015112503000190012981" creportdate="2015-11-23 21:59:48" itotaldefault="0" crealname="花纯虎" cmarstatus="未婚" cidtype="身份证" cidcard="**************0516" icreditcc="4" icreditcu="4"  icreditco="0" icreditcso="0" icreditcg="0" iloanc="0" iloanu="0" iloano="0" iloanso="0" iloang="0" iot="0" icj="0" ice="0" iap="0" ita="0" imi="4" ipi="1" isobtain="1" cadddate="2015-11-17 16:22:22" isapply="1" clastapplydate="" isreapply="0" ><type1><details value="2013年12月25日招商银行发放的贷记卡（美元账户）。截至2015年10月,信用额度折合人民币24,000，已使用额度0。" itype="1" /><details value="2015年7月28日花旗（中国）有限公司发放的贷记卡（美元账户）。截至2015年10月,信用额度折合人民币20,000，尚未激活。" itype="1" /><details value="2013年12月25日招商银行发放的贷记卡（人民币账户）。截至2015年10月，信用额度24,000，已使用额度4,436。" itype="1" /><details value="2015年7月28日花旗（中国）有限公司发放的贷记卡（人民币账户）。截至2015年10月，信用额度20,000，已使用额度0。" itype="1" /></type1><type3><details value="2013年12月19日 招商银行信用卡中心/CMBUSER001 信用卡审批" itype="3" /><details value="2015年7月27日 花旗（中国）有限公司/jy98738 信用卡审批" itype="3" /></type3><type4><details value="2015年11月2日 本人 本人查询(互联网个人信用信息服务平台)" itype="4" /><details value="2015年11月13日 本人 本人查询(互联网个人信用信息服务平台)" itype="4" /></type4></row></Resp>

		zxCheckMsgRecived:检测是否已收到申请回执短信
			附加参数:无
			示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxCheckMsgRecived
			返回:
				<?xml version="1.0" encoding="UTF-8"?><Resp code="0" desc="请求失败"></Resp>
				<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="未收到短信验证码"></Resp>
				<?xml version="1.0" encoding="UTF-8"?><Resp code="2" desc="已收到短信验证码"></Resp>

		zxCheckApplyStatus:查询申请状态
			附加参数:无
				示例:zxPublic.go?sign=8A5880E44DB942F291F03D6A47511889&from=1&cuserId=no99es&method=zxCheckApplyStatus
				返回:
					<?xml version="1.0" encoding="UTF-8"?><Resp code="1" desc="未申请"></Resp>
					<?xml version="1.0" encoding="UTF-8"?><Resp code="2" desc="处理中"></Resp>
					<?xml version="1.0" encoding="UTF-8"?><Resp code="3" desc="已生成"></Resp>
					<?xml version="1.0" encoding="UTF-8"?><Resp code="0" desc="请求失败"></Resp>

流程:
1.登录
	zxVerifyCode 获取图片验证码 type=0
	zxLogin 登录

2.登出
	zxLoginOut 退出登录

3.注册
	zxVerifyCode 获取图片验证码 type=1
	zxCheckIdentity 检验身份证是否正确
	zxCheckRegLoginnameHasUsed 检验用户名是否可用
	zxGetAcvitaveCode 获取短信验证码
	zxRegistered 注册

4.忘记、重置密码
	zxVerifyCode 获取图片验证码 type=2
	zxGoToResetPwd 校验基本信息
	zxGetResetAcvitaveCode 获取短信验证码
	zxGetResetQuestions 获取问题
	zxApplyResetPwd 提交重置申请

5.找回登录名
	zxVerifyCode 获取图片验证码 type=3
	zxFindLoginName (成功后会立刻收到短信)

6.获取征信报告
	【以下操作需要先登录成功,参照流程1】
	zxGetQuestions 获取征信问题
	code="1" 问题认证:
		zxApplyReport 提交申请
		zxViewReport 获取信用报告,成功后会存入数据库，用 queryUserCreditreFerence 可以查询
	code="3" 短信认证:
		zxSpeedApplyReport 提交申请
		zxViewReport 获取信用报告,成功后会存入数据库，用 queryUserCreditreFerence 可以查询










