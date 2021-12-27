//package com.example.demo;
//
//import ch.qos.logback.core.util.ContextUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.CollectionUtils;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @description:
// * @author: liuyanqun1
// * @createTime 2021年12月18日 17:27:00
// **/
//
//public class Demo {
//
//    private static final Logger log = LoggerFactory.getLogger(Test.class);
//
//    /**
//     * 获取某个案例
//     * 当前登陆人一度好友
//     * 的捐款top10
//     *
//     * @param context
//     * @param infoUuid
//     * @return
//     */
//    public LoveRankInfoVo queryFriendsRank(ContextUtil context,String infoUuid) {
//        log.info("queryfriendsrank param:{},infoUuid:{}", context, infoUuid);
//        //登陆用户的userId
//        Long userId = context.getAdminUserId;
//        if (userId <= 0) {
//            throw new UserNotFindException("用户不存在");
//        }
//        LoveRankInfoVo vo = new LoveRankVo();
//        //从缓存获取案例信息
//        String key = "get_caseInfo_" + infoUuid;
//        CrowdfundingInfo fundingInfo = crowdfundingInfoCache.getKey(key);
//        if (fundingInfo == null) thow new IllegalArgumenException("案例不存在");
//        Long caseId = fundingInfo.getId();
//        vo.setCaseId(caseId);
//        //默认跳转
//        vo.setIsFlag(true);
//        //案例捐款金额
//        double caseAmount = fundingInfo.getAmount();
//        //保存当前登陆人的所有一度好友
//        Set<Long> allFriends = new Collections.emptySet();
//        int maxSize = 1000;
//        int nextId = 0;
//        int loopCount;
//        //分页获取登陆人的一度好友
//        while (true) {
//            RealTimeDo realTimeDo = friendsClient.getFriendsuserId(userId, maxSize, nextId);
//            if (realTimeDo == null) {
//                break;
//            }
//            nextId = realTimeDo.getNextId();
//            Set<Long> friends = parseuseridsbydo(realTimeDo);
//            allFriends.addAll(friends);
//            //保护逻辑，防止无限循环
//            loopCount++;
//            if (loopCount > 100) {
//                log.info("获取好友循环次数过多，userId:" + userId);
//                break;
//            }
//        }
//        // 如果当前案例有捐款金额，且登陆人没有一度好友， 不可以可以跳转二级页面
//        if (CollectionUtils.isEmpty(allFriends) || (allFriends.size() == 1 && allFriends.contains(userId) && caseAmount <= 0)) {
//            vo.setDonateAmount(caseAmount);
//            vo.setIsFlag(false);
//            return vo;
//        }
//        //获取一度好友捐款金额记录
//        List<CrowdfundingOrder> orderList = crowdfundingOrderBiz.getByUserId(allFriends, caseId);
//        if (CollectionUtils.isEmpty(orderList)) {
//            log.info("一度好友没有捐款记录，userIds:{}", allFriends);
//            vo.setDonateAmount(caseAmount);
//            return vo;
//        }
//        double sum = orderList.stream().mapToDouble(CrowdfundingOrder::getAmount).sum();
//        //一度好友捐款金额
//        List<UserDonateVo> userDonateVos = buildUserDonate(orderList);
//        vo.setFriendsAmount(sum);
//        vo.setLovePepoleAmount(caseAmount - sum);
//        //捐款userId集合
//        Set<Long> donateUserIds = userDonateVos.stream().collect(Collectors.toSet(CrowdfundingOrder::getUserId));
//        //获取用户基本信息
//        List<UserInfo> userInfos = userClient.findUserList(donateUserIds);
//        if (CollectionUtils.isNotEmpty(userInfos)) {
//            log.info("查询用户信息为空 userIds:{}", donateUserIds);
//            vo.setDonateAmount(caseAmount);
//            return vo;
//        }
//        Map<Long, UserInfo> userInfoMap = userInfos.stream().collect(Collectors.toMap(UserInfo::getUserId, UserInfo -> UserInfo));
//        //获取用户点赞信息
//        List<UserLikeVo> likeVo = userLikeService.getUserIds(donateUserIds);
//        Map<Long,Integer> likeMap = new HashMap<>();
//        if (CollectionUtils.isNotEmpty(likeVo)){
//           likeMap = likeVo.stream().collect(Collectors.toMap(UserLikeVo::getUserId,UserLikeVo::getLikeCount));
//        }
//        //封装用户排行榜信息
//        for (UserDonateVo donateVo : userDonateVos) {
//            Long donateUser = donateVo.getUserId();
//            UserInfo userInfo = userInfoMap.get(donateUser);
//            if (null != userInfo) {
//                donateVo.setNickName(userInfo.getName());
//                donateVo.setImg(userInfo.getImg());
//            }
//            int count = likeMap.get(donateUser);
//            donateVo.setLike(count);
//        }
//        //金额排序，取top10
//        userDonateVos = userDonateVos.stream().sorted(Comparator.comparing(UserDonateVo::getAmount).reversed()).limit(10).collect(Collectors.toList());
//        vo.setUserDonateList(userDonateVos);
//        return vo;
//    }
//
//    /**
//     * 解析成一个人 捐款金额
//     *
//     * @param orderList
//     * @return 排行榜Vo
//     */
//    public List<UserDonateVo> buildUserDonate(List<CrowdfundingOrder> orderList) {
//        List<UserDonateVo> list = new ArrayList();
//        if (CollectionUtils.isEmpty(orderList)) {
//            return Lists.newArrayList();
//        }
//        /**
//         * 省略解析过程
//         */
//        return list;
//    }
//
//    /**
//     * 解析一度好友列表
//     *
//     * @param data
//     * @return 一度好友Set
//     */
//    private Set<Long> parseuseridsbydo(RealTimeDo data) {
//        try {
//            if (data == null) {
//                return Collections.emptySet();
//            }
//            FriendDo friend = data.getFriend();
//            if (friend == null) {
//                return Collections.emptySet();
//            }
//            /**
//             * …………
//             * 省略一系列其他的判断
//             * …………
//             */
//            return friend.getFriends_ids();
//        } catch (Exception ex) {
//            log.error("解析一度好友错误,exception:{}", ex);
//        }
//        return Collections.emptySet();
//    }
//
//    /**
//     * 用户排行榜Vo
//     */
//    class UserDonateVo{
//
//        private String nickName;
//
//        private String img;
//
//        private double amount;
//
//        private int like;
//
//    }
//}
