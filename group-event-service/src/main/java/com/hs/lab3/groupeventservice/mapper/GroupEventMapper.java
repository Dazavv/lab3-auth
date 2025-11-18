package com.hs.lab3.groupeventservice.mapper;

import com.hs.lab3.groupeventservice.dto.responses.GroupEventDto;
import com.hs.lab3.groupeventservice.entity.GroupEvent;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GroupEventMapper {
    GroupEventDto toGroupEventDto(GroupEvent groupEvent);

    List<GroupEventDto> toGroupEventDtoList(List<GroupEvent> groupEvents);
}
