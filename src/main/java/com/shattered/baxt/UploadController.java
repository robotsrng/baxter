package com.shattered.baxt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class UploadController {

    public static String BASE = File.separator + "home" + File.separator + "srng" + File.separator + "Documents" + File.separator + "baxter";
    public static String userFileDir = BASE + File.separator + "user_files" + File.separator;
    public static String userUploadDir = File.separator + "uploaded" + File.separator;
    public static String userSoundDir = File.separator + "sound" + File.separator;
    public static String userWaveDir = File.separator + "waveform" + File.separator;
    public static String finalVidDir = File.separator + "baxted_vids" + File.separator;

    public UploadController() {

        checkInitDirectories();
    }

    private void checkInitDirectories() {
        File dir = new File(userFileDir);
        if (!dir.isDirectory() && !dir.exists()) {
            dir.mkdirs();
        }
    }

    public static int uploadVideos(MultipartFile[] mpFiles, String username) {
        int code = 0;
        for (MultipartFile mpFile : mpFiles) {
            long fileSize = mpFile.getSize();
            if (fileSize / 1000000 > 500) {
                code++;
            }
            if ((mpFile.getSize() / 1000000) + getUserStorage(username) > 50000) {
                System.out.println("to much storage");
                return -1;
            }
            String origFilename = mpFile.getOriginalFilename();
            if (FilenameUtils.getExtension(origFilename).equals("mp4") || FilenameUtils.getExtension(origFilename).equals("mov") || FilenameUtils.getExtension(origFilename).equals("MOV")) {
                File tempFile = null;
                try {
                    tempFile = new File(userFileDir + username + userUploadDir + mpFile.getOriginalFilename());
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(mpFile.getBytes());
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("upload - " + tempFile.getName());
            } else {
                code++;
            }
        }
        return code;
    }

    public static boolean uploadSound(MultipartFile soundFile, String username) {
        File tempFile = null;
        if (FilenameUtils.getExtension(soundFile.getOriginalFilename()).equals("wav") || FilenameUtils.getExtension(soundFile.getOriginalFilename()).equals("mp3")) {
            try {
                tempFile = new File(userFileDir + username + userSoundDir + soundFile.getOriginalFilename());
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(soundFile.getBytes());
                fos.close();
                if (FilenameUtils.getExtension(soundFile.getOriginalFilename()).equals("wav")) {
                    AudioWaveformCreator.createWave(userFileDir + username + userSoundDir + soundFile.getOriginalFilename(), userFileDir + username + userWaveDir + soundFile.getOriginalFilename().replace("wav", "png"));
                } else if (FilenameUtils.getExtension(soundFile.getOriginalFilename()).equals("mp3")) {
                    AudioWaveformCreator.createWave(userFileDir + username + userSoundDir + soundFile.getOriginalFilename(), userFileDir + username + userWaveDir + soundFile.getOriginalFilename().replace("mp3", "png"));
                }
                System.out.println("upload - " + tempFile.getName());
                if (tempFile.getName().endsWith("mp3")) {
                    tempFile.delete();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean removeFile(String filepath, String username) {
        File file = new File(filepath);
        if (file.getName().endsWith("wav")) {
            File waveFile = new File(userFileDir + username + userWaveDir + file.getName().replace("wav", "png"));
            waveFile.delete();
        }
        return (file.delete());
    }

    public static boolean renameFile(String filepath, String username, String newName) {
        File file = new File(filepath);
        if (FilenameUtils.getExtension(file.getName()).equals("wav")) {
            renameWaveForm(filepath, username, newName);
        }
        File file2 = null;
        try {
            file2 = new File((file.getParentFile() + File.separator + newName + "." + (FilenameUtils.getExtension(file.getName()))));
            Files.move(file.toPath(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (file.delete());
    }

    private static boolean renameWaveForm(String filepath, String username, String newName) {
        File file = new File(userFileDir + username + userWaveDir + (new File(filepath.replace("wav", "png"))).getName());
        File file2 = null;
        try {
            file2 = new File((file.getParentFile() + File.separator + newName + "." + (FilenameUtils.getExtension(file.getName()))));
            Files.move(file.toPath(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (file.delete());
    }

    public static long getUserStorage(String currentUserName) {
        Path folder = Paths.get(userFileDir + currentUserName);
        long size = -1;
        try {
            size = Files.walk(folder)
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size / 1000000;
    }

    public static List<FileDisplayHolder> getUserSoundFiles(String username) {
        List<FileDisplayHolder> files = new ArrayList<>();
        for (File file : new File(userFileDir + username + userSoundDir).listFiles()) {
            files.add(new FileDisplayHolder(file, FilenameUtils.removeExtension(file.getName())));
        }
        return files;
    }

    public static File[] getUserVideoFiles(String username) {
        return new File(userFileDir + username + userUploadDir).listFiles();
    }

    public static List<FileDisplayHolder> getUserVideoFilesDisplay(String username) {
        List<FileDisplayHolder> files = new ArrayList<>();
        for (File file : new File(userFileDir + username + userUploadDir).listFiles()) {
            files.add(new FileDisplayHolder(file, FilenameUtils.removeExtension(file.getName())));
        }
        return files;
    }

    static List<FileDisplayHolder> getUserCompletedFiles(String username) {
        List<FileDisplayHolder> files = new ArrayList<>();
        for (File file : new File(userFileDir + username + finalVidDir).listFiles()) {
            files.add(new FileDisplayHolder(file, FilenameUtils.removeExtension(file.getName())));
        }
        return files;
    }

    public static String getUserSoundDir(String filename, String currentUserName) {
        File file = new File(userFileDir + currentUserName + userSoundDir + filename);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }

    public static String getUserWaveDir(String filename, String currentUserName) {
        File file = new File(userFileDir + currentUserName + userWaveDir + filename.replace("wav", "png"));
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }

    public static String getUserOutputDir(String username) {
        return userFileDir + username + finalVidDir;
    }

    public static byte[] getImage(String imageName, String username) throws IOException {
        File serverFile = new File(userFileDir + username + userWaveDir + imageName.replace("wav", "png"));
        return Files.readAllBytes(serverFile.toPath());
    }

    public static boolean checkUserDir(String username) {
        File userDir;
        if (!(userDir = new File(userFileDir + username + userSoundDir)).exists()) {
            userDir.mkdirs();
        }
        if (!(userDir = new File(userFileDir + username + userUploadDir)).exists()) {
            userDir.mkdirs();
        }
        if (!(userDir = new File(userFileDir + username + finalVidDir)).exists()) {
            userDir.mkdirs();
        }
        if (!(userDir = new File(userFileDir + username + userWaveDir)).exists()) {
            userDir.mkdirs();
        }
        return true;
    }


}
