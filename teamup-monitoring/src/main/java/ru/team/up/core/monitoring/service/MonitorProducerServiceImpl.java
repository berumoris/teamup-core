package ru.team.up.core.monitoring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.team.up.dto.*;
import ru.team.up.core.entity.*;

import java.util.*;

@Slf4j
@Service
public class MonitorProducerServiceImpl implements MonitorProducerService {

    private KafkaTemplate kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topic;

    public MonitorProducerServiceImpl(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public Map<String, Object> parameters(String key, Object value) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(key, value);
        return parameters;
    }

    @Override
    public ReportDto constructReportDto(Object principal, ControlDto control,
                                        Class cl, String param1, Object param2) {
        ReportDto reportDto = ReportDto.builder()
                .control(control)
                .reportName(cl.getSimpleName())
                .reportStatus(Optional.of(param2).isEmpty() ||
                        (Integer) param2 == 0 ? ReportStatusDto.FAILURE : ReportStatusDto.SUCCESS)
                .time(new Date())
                .parameters(parameters(param1, param2)).build();

        if (principal.toString().equals("anonymousUser")) {
            reportDto.setInitiatorId(0L);
            reportDto.setInitiatorName("anonymousUser");
            reportDto.setInitiatorType(InitiatorTypeDto.USER);
        } else if (principal instanceof Admin) {
            Admin admin = (Admin) principal;
            reportDto.setInitiatorId(admin.getId());
            reportDto.setInitiatorName(admin.getUsername());
            reportDto.setInitiatorType(InitiatorTypeDto.ADMIN);
        } else if (principal instanceof User) {
            User user = (User) principal;
            reportDto.setInitiatorId(user.getId());
            reportDto.setInitiatorName(user.getUsername());
            reportDto.setInitiatorType(InitiatorTypeDto.USER);
        } else if (principal instanceof Moderator) {
            Moderator moderator = (Moderator) principal;
            reportDto.setInitiatorId(moderator.getId());
            reportDto.setInitiatorName(moderator.getUsername());
            reportDto.setInitiatorType(InitiatorTypeDto.MANAGER);
        } else {
            log.warn("???? ?????????????? ???????????????? ???????????? ???? ?????????????? Principal");
        }

        String[] appModuleName = cl.getPackageName().split("\\.");
        switch (appModuleName[3]) {
            case ("app"):
                reportDto.setAppModuleName(AppModuleNameDto.TEAMUP_APP);
                break;
            case ("auth"):
                reportDto.setAppModuleName(AppModuleNameDto.TEAMUP_AUTH);
                break;
            case ("core"):
                reportDto.setAppModuleName(AppModuleNameDto.TEAMUP_CORE);
                break;
            case ("external"):
                reportDto.setAppModuleName(AppModuleNameDto.TEAMUP_EXTERNAL);
                break;
            case ("input"):
                reportDto.setAppModuleName(AppModuleNameDto.TEAMUP_INPUT);
                break;
            case ("kafka"):
                reportDto.setAppModuleName(AppModuleNameDto.TEAMUP_KAFKA);
                break;
            case ("moderator"):
                reportDto.setAppModuleName(AppModuleNameDto.TEAMUP_MODERATOR);
                break;
            case ("monitoring"):
                reportDto.setAppModuleName(AppModuleNameDto.TEAMUP_MONITORING);
                break;
            case ("sup"):
                reportDto.setAppModuleName(AppModuleNameDto.TEAMUP_SUP);
                break;
            default:
                log.warn("\"???? ?????????????? ???????????????????? ????????????\"");
        }
        return reportDto;
    }

    @Override
    public void send(ReportDto content) {
        log.debug("topic: " + topic + " content (ReportDro): " + content);
        kafkaTemplate.send(topic, content);
    }
}
