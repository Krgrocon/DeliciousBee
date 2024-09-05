package com.example.deliciousBee.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.file.MemberAttachedFile;
import com.example.deliciousBee.model.mypage.MyPage;


@Component
public class MemberFileService {
    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 업로드 된 파일을 지정된 경로에 저장하고, 저장된 파일명을 리턴
     * @param mfile 업로드 된 파일
     * @param path 저장한 경로
     * @return 저장된 파일명
     */
    
    
    public MemberAttachedFile saveFile(MultipartFile mfile) {
        if (mfile == null || mfile.isEmpty() || mfile.getSize() == 0) {
            return null;
        }

        File path = new File(uploadPath + "/myPageImage");
        if (!path.isDirectory()) {
            path.mkdirs();
        }

        String originalFilename = mfile.getOriginalFilename();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String savedFilename = sdf.format(new Date());

        String ext;
        int lastIndex = originalFilename.lastIndexOf(".");
        ext = (lastIndex == -1) ? "" : "." + originalFilename.substring(lastIndex + 1);

        File serverFile = null;
        while (true) {
            serverFile = new File(path, savedFilename + ext);
            if (!serverFile.isFile()) break;
            savedFilename = savedFilename + new Date().getTime();
        }

        try {
            mfile.transferTo(serverFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return new MemberAttachedFile(originalFilename, savedFilename + ext, mfile.getSize());
    }

    /**
     * 서버에 저장된 파일의 전체 경로를 전달받아, 해당 파일을 삭제
     * @param fullpath 삭제할 파일의 경로
     * @return 삭제 여부
     */
    public boolean deleteFile(String fullpath) {
        // 파일 삭제 로직 추가
        File file = new File(fullpath);
        return file.exists() && file.delete();
    }

}
