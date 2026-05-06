package com.learning.englishpro.common.service.impl;

import com.learning.englishpro.common.service.MediaMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.io.File;

@Service
@Slf4j
public class MediaMetadataServiceImpl implements MediaMetadataService {

    @Override
    public Integer extractDurationSeconds(File file) {
        try {
            MultimediaObject media = new MultimediaObject(file);
            MultimediaInfo info = media.getInfo();
            if (info != null && info.getDuration() > 0) {
                // getDuration() returns milliseconds
                return (int) (info.getDuration() / 1000L);
            }
        } catch (Exception e) {
            log.warn("Không thể lấy thời lượng của file {}: {}", file.getName(), e.getMessage());
        }
        return null;
    }
}
