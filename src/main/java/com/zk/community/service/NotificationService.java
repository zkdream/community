package com.zk.community.service;

import com.zk.community.dto.NotificationDTO;
import com.zk.community.dto.PaginationDTO;
import com.zk.community.dto.QuestionDTO;
import com.zk.community.enums.NotificationStatusEnum;
import com.zk.community.enums.NotificationTypeEnum;
import com.zk.community.exception.CustomizeErrorCode;
import com.zk.community.exception.CustomizeException;
import com.zk.community.mapper.NotificationMapper;
import com.zk.community.mapper.UserMapper;
import com.zk.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;



@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();

        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(example);
//        Integer totalCount = questionMapper.countByUserId(userId);
        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) {
            page = 1;
        } else if (page > totalPage) {
            page = totalCount;
        }
        paginationDTO.setPagination(totalPage, page);
        Integer offset = size * (page - 1);
        NotificationExample example1 = new NotificationExample();
        example1.createCriteria()
                .andReceiverEqualTo(userId);
        example1.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example1, new RowBounds(offset, size));
        if (notifications.size() == 0) {
            return paginationDTO;
        }

//        Set<Long> disUserId = notifications.stream().map(notify -> notify.getNotifier()).collect(Collectors.toSet());
//        ArrayList<Long> userIds = new ArrayList<>(disUserId);
//        UserExample userExample = new UserExample();
//        userExample.createCriteria()
//                .andIdIn(userIds);
//        List<User> users = userMapper.selectByExample(userExample);
//        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(u -> u.getId(), u -> u));


        List<NotificationDTO> notificationDTOS = new ArrayList<NotificationDTO>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfTYpe(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public Long unreadCount(Long id) {
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andReceiverEqualTo(id)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(example);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification==null){
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FIND);
        }
        if (notification.getReceiver()!=user.getId()){
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfTYpe(notification.getType()));
        return notificationDTO;
    }
}
