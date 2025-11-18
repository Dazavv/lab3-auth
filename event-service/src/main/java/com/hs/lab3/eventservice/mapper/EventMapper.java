package java.com.hs.lab3.eventservice.mapper;

import java.com.hs.lab3.eventservice.dto.responses.EventDto;
import java.com.hs.lab3.eventservice.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
    EventDto toEventDto(Event Event);
    List<EventDto> toEventDtoList(List<Event> events);
}
