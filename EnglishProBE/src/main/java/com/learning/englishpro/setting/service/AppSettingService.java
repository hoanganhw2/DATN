package com.learning.englishpro.setting.service;

import com.learning.englishpro.setting.entity.AppSetting;
import com.learning.englishpro.setting.repository.AppSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppSettingService {

    private final AppSettingRepository repository;

    @Transactional(readOnly = true)
    public Map<String, String> getAllSettings() {
        List<AppSetting> settings = repository.findAll();
        Map<String, String> map = new HashMap<>();
        for (AppSetting setting : settings) {
            map.put(setting.getKey(), setting.getValue());
        }
        return map;
    }

    @Transactional(readOnly = true)
    public String getSetting(String key, String defaultValue) {
        return repository.findById(key)
                .map(AppSetting::getValue)
                .orElse(defaultValue);
    }

    @Transactional
    public void saveSettings(Map<String, String> newSettings) {
        newSettings.forEach((key, value) -> {
            repository.save(new AppSetting(key, value));
        });
    }
}
