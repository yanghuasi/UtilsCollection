package seekbar.ggh.com.utils.file;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import seekbar.ggh.com.myapplication.R;
import seekbar.ggh.com.utils.bean.AppInfo;
import seekbar.ggh.com.utils.bean.FileBean;
import seekbar.ggh.com.utils.bean.ImgFolderBean;
import seekbar.ggh.com.utils.bean.Music;
import seekbar.ggh.com.utils.bean.Video;


/**
 *
 */
@SuppressLint("SimpleDateFormat")
@SuppressWarnings({"rawtypes", "unchecked"})
public final class FileManager {
    private FileManager() {

    }

    /**
     * 方法名: </br>
     * 详述: sd卡是否可用</br>
     * 开发人员：luohf</br>
     * 创建时间：2013年11月14日</br>
     *
     * @return
     */
    public static boolean sdCardCanUse() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    /**
     * 得到文件的输入流，如无法定位文件返回null。
     *
     * @param relativePath 文件相对当前应用程序的类加载器的路径。
     * @return 文件的输入流。
     */
    public static InputStream getResourceStream(String relativePath) {
        return Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(relativePath);
    }

    /**
     * 关闭输入流。
     *
     * @param is 输入流，可以是null。
     */
    public static void closeInputStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeFileOutputStream(FileOutputStream fos) {
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 从文件路径中提取目录路径，如果文件路径不含目录返回null。
     *
     * @param filePath 文件路径。
     * @return 目录路径，不以'/'或操作系统的文件分隔符结尾。
     */
    public static String extractDirPath(String filePath) {
        int separatePos = Math.max(filePath.lastIndexOf('/'), filePath
                .lastIndexOf('\\')); // 分隔目录和文件名的位置
        return separatePos == -1 ? null : filePath.substring(0, separatePos);
    }

    /**
     * 从文件路径中提取文件名, 如果不含分隔符返回null
     *
     * @param filePath
     * @return 文件名, 如果不含分隔符返回null
     */
    public static String extractFileName(String filePath) {
        int separatePos = Math.max(filePath.lastIndexOf('/'), filePath
                .lastIndexOf('\\')); // 分隔目录和文件名的位置
        return separatePos == -1 ? null : filePath.substring(separatePos + 1, filePath.length());
    }

    /**
     * 获取文件的后缀（带 .符号）
     *
     * @param filePath
     * @return
     */
    public static String getFileSuffix(String filePath) {
        if (filePath.contains(".")) {
            return filePath.substring(filePath.lastIndexOf("."), filePath.length());
        } else {
            return "";
        }
    }

    /**
     * 按路径建立文件，如已有相同路径的文件则不建立。
     *
     * @param filePath 要建立文件的路径。
     * @return 表示此文件的File对象。
     * @throws IOException 如路径是目录或建文件时出错抛异常。
     */
    public static File makeFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.isFile())
            return file;
        if (filePath.endsWith("/") || filePath.endsWith("\\"))
            throw new IOException(filePath + " is a directory");

        String dirPath = extractDirPath(filePath); // 文件所在目录的路径

        if (dirPath != null) { // 如文件所在目录不存在则先建目录
            makeFolder(dirPath);
        }

        file.createNewFile();
        return file;
    }

    /**
     * 新建目录,支持建立多级目录
     *
     * @param folderPath 新建目录的路径字符串
     * @return boolean, 如果目录创建成功返回true, 否则返回false
     */
    public static boolean makeFolder(String folderPath) {
        try {
            File myFilePath = new File(folderPath);
            if (!myFilePath.exists()) {
                myFilePath.mkdirs();
                System.out.println("新建目录为：" + folderPath);
            } else {
                System.out.println("目录已经存在: " + folderPath);
            }
        } catch (Exception e) {
            System.out.println("新建目录操作出错");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param filePathAndName 要删除文件名及路径
     * @return boolean 删除成功返回true,删除失败返回false
     */
    public static boolean deleteFile(String filePathAndName) {
        try {
            File myDelFile = new File(filePathAndName);
            if (myDelFile.exists()) {
                myDelFile.delete();
            }
        } catch (Exception e) {
            System.out.println("删除文件操作出错");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 递归删除指定目录中所有文件和子文件夹
     *
     * @param path           某一目录的路径,如"c:\cs"
     * @param ifDeleteFolder boolean值,如果传true,则删除目录下所有文件和文件夹;如果传false,则只删除目录下所有文件,子文件夹将保留
     */
    public static void deleteAllFile(String path, boolean ifDeleteFolder) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        String temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith("\\") || path.endsWith("/"))
                temp = path + tempList[i];
            else
                temp = path + File.separator + tempList[i];
            if ((new File(temp)).isFile()) {
                deleteFile(temp);
            } else if ((new File(temp)).isDirectory() && ifDeleteFolder) {
                deleteAllFile(path + File.separator + tempList[i],
                        ifDeleteFolder);// 先删除文件夹里面的文件
                deleteFolder(path + File.separator + tempList[i]);// 再删除空文件夹
            }
        }
    }

    /**
     * 删除文件夹,包括里面的文件
     *
     * @param folderPath 文件夹路径字符串
     */
    public static void deleteFolder(String folderPath) {
        try {
            File myFilePath = new File(folderPath);
            if (myFilePath.exists()) {
                deleteAllFile(folderPath, true); // 删除完里面所有内容
                myFilePath.delete(); // 删除空文件夹
            }
        } catch (Exception e) {
            System.out.println("删除文件夹操作出错");
            e.printStackTrace();
        }
    }


    public static void copyFile(InputStream sourceInStream, String targetPath) {
        InputStream inStream = null;
        FileOutputStream fos = null;

        try {
//			int byteSum = 0;
            int byteRead = 0;
            if (sourceInStream != null) { // 文件存在时
                inStream = sourceInStream; // 读入原文件
                String dirPath = extractDirPath(targetPath); // 文件所在目录的路径
                if (dirPath != null) { // 如文件所在目录不存在则先建目录
                    makeFolder(dirPath);
                }
                fos = new FileOutputStream(targetPath);
                byte[] buffer = new byte[1444];
                while ((byteRead = inStream.read(buffer)) != -1) {
//					byteSum += byteRead; // 字节数 文件大小
                    fos.write(buffer, 0, byteRead);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeInputStream(inStream);
            closeFileOutputStream(fos);
        }
    }

    /**
     * 复制文件,如果目标文件的路径不存在,会自动新建路径
     *
     * @param sourcePath 源文件路径, e.g. "c:/cs.txt"
     * @param targetPath 目标文件路径 e.g. "f:/bb/cs.txt"
     */
    public static void copyFile(String sourcePath, String targetPath) {
        InputStream inStream = null;
        FileOutputStream fos = null;

        try {
//			int byteSum = 0;
            int byteRead = 0;
            File sourcefile = new File(sourcePath);
            if (sourcefile.exists()) { // 文件存在时
                inStream = new FileInputStream(sourcePath); // 读入原文件
                String dirPath = extractDirPath(targetPath); // 文件所在目录的路径
                if (dirPath != null) { // 如文件所在目录不存在则先建目录
                    makeFolder(dirPath);
                }
                fos = new FileOutputStream(targetPath);
                byte[] buffer = new byte[1444];
                while ((byteRead = inStream.read(buffer)) != -1) {
//					byteSum += byteRead; // 字节数 文件大小
                    fos.write(buffer, 0, byteRead);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeInputStream(inStream);
            closeFileOutputStream(fos);
        }
    }

    /**
     * 将路径和文件名拼接起来
     *
     * @param folderPath 某一文件夹路径字符串，e.g. "c:\cs\" 或 "c:\cs"
     * @param fileName   某一文件名字符串, e.g. "cs.txt"
     * @return 文件全路径的字符串
     */
    public static String makeFilePath(String folderPath, String fileName) {
        return folderPath.endsWith("\\") || folderPath.endsWith("/") ? folderPath
                + fileName
                : folderPath + File.separatorChar + fileName;
    }

    /**
     * 将某一文件夹下的所有文件和子文件夹拷贝到目标文件夹，若目标文件夹不存在将自动创建
     *
     * @param sourcePath 源文件夹字符串，e.g. "c:\cs"
     * @param targetPath 目标文件夹字符串，e.g. "d:\tt\qq"
     */
    public static void copyFolder(String sourcePath, String targetPath) {
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            makeFolder(targetPath); // 如果文件夹不存在 则建立新文件夹
            String[] file = new File(sourcePath).list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                String tempPath = makeFilePath(sourcePath, file[i]);
                temp = new File(tempPath);
                if (temp.isFile()) {
                    input = new FileInputStream(temp);
                    output = new FileOutputStream(makeFilePath(
                            targetPath, file[i]));
                    byte[] b = new byte[1024 * 5];
                    int len = 0;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    closeInputStream(input);
                    closeFileOutputStream(output);
                } else if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(sourcePath + '/' + file[i], targetPath + '/'
                            + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        } finally {
            closeInputStream(input);
            closeFileOutputStream(output);
        }
    }

    /**
     * 移动文件
     *
     * @param oldFilePath 旧文件路径字符串, e.g. "c:\tt\cs.txt"
     * @param newFilePath 新文件路径字符串, e.g. "d:\kk\cs.txt"
     */
    public static void moveFile(String oldFilePath, String newFilePath) {
        copyFile(oldFilePath, newFilePath);
        deleteFile(oldFilePath);
    }

    /**
     * 移动文件夹
     *
     * @param oldFolderPath 旧文件夹路径字符串，e.g. "c:\cs"
     * @param newFolderPath 新文件夹路径字符串，e.g. "d:\cs"
     */
    public static void moveFolder(String oldFolderPath, String newFolderPath) {
        copyFolder(oldFolderPath, newFolderPath);
        deleteFolder(oldFolderPath);

    }

    /**
     * 获得某一文件夹下的所有文件的路径集合
     *
     * @param filePath 文件夹路径
     * @return ArrayList，其中的每个元素是一个文件的路径的字符串
     */
    public static ArrayList getFilePathFromFolder(String filePath) {
        ArrayList fileNames = new ArrayList();
        File file = new File(filePath);
        try {
            File[] tempFile = file.listFiles();
            for (int i = 0; i < tempFile.length; i++) {
                if (tempFile[i].isFile()) {
                    String tempFileName = tempFile[i].getName();
                    fileNames.add(makeFilePath(filePath, tempFileName));
                }
            }
        } catch (Exception e) {
            fileNames.add("尚无文件到达！");
        }
        return fileNames;
    }

    /**
     * 获得某一文件夹下的所有图片文件的路径集合
     *
     * @param
     * @return ArrayList，其中的每个元素是一个文件的路径的字符串
     */
    public static List<String> getImagePathFromFolder(String dir) {
//        ArrayList filePaths = new ArrayList();
        List<String> filePaths = new ArrayList<String>();
//        ContentResolver mContentResolver = context.getContentResolver();
//        // 扫描图片
//        Cursor c = null;
//        try {
//            c = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
//                    MediaStore.Images.Media.MIME_TYPE + "= ? or " + MediaStore.Images.Media.MIME_TYPE + "= ?",
//                    new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
//            while (c.moveToNext()) {
//                String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));// 路径
//                File parentFile = new File(path).getParentFile();
//                String dir = parentFile.getAbsolutePath();
        File dirFile = FileUtils.getFileByPath(dir);

//        File file = new File(filePath);
        try {
            String[] tempFiles = dirFile.list();
            for (String fs:tempFiles) {
                String filePath = dir + "/" + fs;
                boolean isFile = FileUtils.isFile(filePath);

                    if (isFile) {

                        filePaths.add(filePath);
                    }

            }
//                } catch (Exception e) {
//                    filePaths.add("尚无文件到达！");
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("0", "e" + e);
        }
        return filePaths;
    }

    /**
     * 获得某一文件夹下的所有TXT，txt文件名的集合
     *
     * @param filePath 文件夹路径
     * @return ArrayList，其中的每个元素是一个文件名的字符串
     */
    public static ArrayList getFileNameFromFolder(String filePath) {
        ArrayList fileNames = new ArrayList();
        File file = new File(filePath);
        File[] tempFile = file.listFiles();
        for (int i = 0; i < tempFile.length; i++) {
            if (tempFile[i].isFile())
                fileNames.add(tempFile[i].getName());
        }
        return fileNames;
    }

    /**
     * 获得某一路径下所以文件夹名称的集合
     *
     * @param filePath
     * @return
     */
    public static ArrayList getFolderNameFromFolder(String filePath) {
        ArrayList fileNames = new ArrayList();
        File file = new File(filePath);
        File[] tempFile = file.listFiles();
        for (int i = 0; i < tempFile.length; i++) {
            if (tempFile[i].isDirectory())
                fileNames.add(tempFile[i].getName());
        }
        return fileNames;
    }

    /**
     * 获得某一文件夹下的所有文件的总数
     *
     * @param filePath 文件夹路径
     * @return int 文件总数
     */
    public static int getFileCount(String filePath) {
        int count = 0;
        try {
            File file = new File(filePath);
            if (!isFolderExist(filePath)) return count;
            File[] tempFile = file.listFiles();
            for (int i = 0; i < tempFile.length; i++) {
                if (tempFile[i].isFile())
                    count++;
            }
        } catch (Exception fe) {
            count = 0;
        }
        return count;
    }

    /**
     * 获得某一路径下要求匹配的文件的个数
     *
     * @param filePath 文件夹路径
     * @param matchs   需要匹配的文件名字符串,如".*a.*",如果传空字符串则不做匹配工作
     *                 直接返回路径下的文件个数
     * @return int 匹配文件名的文件总数
     */
    public static int getFileCount(String filePath, String matchs) {
        int count = 0;
        if (!isFolderExist(filePath)) return count;
        if (matchs.equals("") || matchs == null) return getFileCount(filePath);
        File file = new File(filePath);
        File[] tempFile = file.listFiles();
        for (int i = 0; i < tempFile.length; i++) {
            if (tempFile[i].isFile())
                if (Pattern.matches(matchs, tempFile[i].getName()))
                    count++;
        }
        return count;
    }

    public static int getStrCountFromFile(String filePath, String str) {
        if (!isFileExist(filePath)) return 0;
        FileReader fr = null;
        BufferedReader br = null;
        int count = 0;
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(str) != -1) count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * 获得某一文件的行数
     *
     * @param filePath 文件夹路径
     * @return int 行数
     */
    @SuppressWarnings("unused")
    public static int getFileLineCount(String filePath) {
        if (!isFileExist(filePath))
            return 0;
        FileReader fr = null;
        BufferedReader br = null;
        int count = 0;
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //System.out.println("count="+count);
        return count;
    }

    /**
     * 判断某一文件是否为空
     *
     * @param filePath 文件的路径字符串，e.g. "c:\cs.txt"
     * @return 如果文件为空返回true, 否则返回false
     * @throws IOException
     */
    public static boolean ifFileIsNull(String filePath) throws IOException {
        boolean result = false;
        FileReader fr = new FileReader(filePath);
        if (fr.read() == -1) {
            result = true;
            System.out.println(filePath + " 文件为空!");
        } else {
            System.out.println(filePath + " 文件不为空!");
        }
        fr.close();
        return result;
    }

    /**
     * 判断文件是否存在
     *
     * @param fileName 文件路径字符串，e.g. "c:\cs.txt"
     * @return 若文件存在返回true, 否则返回false
     */
    public static boolean isFileExist(String fileName) {
        // 判断文件名是否为空
        if (fileName == null || fileName.length() == 0) {
            return false;
        } else {
            // 读入文件 判断文件是否存在
            File file = new File(fileName);
            if (!file.exists() || file.isDirectory()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断文件夹是否存在
     *
     * @param folderPath，文件夹路径字符串，e.g. "c:\cs"
     * @return 若文件夹存在返回true, 否则返回false
     */
    public static boolean isFolderExist(String folderPath) {
        File file = new File(folderPath);
        return file.isDirectory() ? true : false;
    }

    /**
     * 获得文件的大小
     *
     * @param filePath 文件路径字符串，e.g. "c:\cs.txt"
     * @return 返回文件的大小, 单位kb, 如果文件不存在返回null
     */
    public static Double getFileSize(String filePath) {
        if (!isFileExist(filePath))
            return null;
        else {
            File file = new File(filePath);
            double intNum = Math.ceil(file.length() / 1024.0);
            return Double.valueOf(intNum);
        }
    }


    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 计算SD卡的剩余空间
     *
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getFreeDiskSpace() {
        String status = Environment.getExternalStorageState();
        long freeSpace = 0;
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                freeSpace = availableBlocks * blockSize / 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return (freeSpace);
    }


    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 获得文件的大小,字节表示
     *
     * @param filePath 文件路径字符串，e.g. "c:\cs.txt"
     * @return 返回文件的大小, 单位kb, 如果文件不存在返回null
     */
    public static Double getFileByteSize(String filePath) {
        if (!isFileExist(filePath))
            return null;
        else {
            File file = new File(filePath);
            double intNum = Math.ceil(file.length() / 1024.0);
            return Double.valueOf(intNum);
        }

    }

    /**
     * 获得文件的最后修改时间
     *
     * @param filePath 文件路径字符串，e.g. "c:\cs.txt"
     * @return 返回文件最后的修改日期的字符串, 如果文件不存在返回null
     */
    public static String fileModifyTime(String filePath) {
        if (!isFileExist(filePath)) return null;
        else {
            File file = new File(filePath);

            long timeStamp = file.lastModified();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            String tsForm = formatter.format(new Date(timeStamp));
            return tsForm;
        }
    }

    /**
     * 遍历某一文件夹下的所有文件,返回一个ArrayList,每个元素又是一个子ArrayList,
     * 子ArrayList包含三个字段,依次是文件的全路径(String),文件的修改日期(String),
     * 文件的大小(Double)
     *
     * @param folderPath, 某一文件夹的路径
     * @return ArrayList
     */
    public static ArrayList getFilesSizeModifyTime(String folderPath) {
        List returnList = new ArrayList();
        List filePathList = getFilePathFromFolder(folderPath);
        for (int i = 0; i < filePathList.size(); i++) {
            List tempList = new ArrayList();
            String filePath = (String) filePathList.get(i);
            String modifyTime = FileManager.fileModifyTime(filePath);
            Double fileSize = FileManager.getFileSize(filePath);
            tempList.add(filePath);
            tempList.add(modifyTime);
            tempList.add(fileSize);
            returnList.add(tempList);
        }
        return (ArrayList) returnList;
    }

    /**
     * 获得某一文件夹下的所有TXT，txt文件名的集合
     *
     * @param filePath 文件夹路径
     * @return ArrayList，其中的每个元素是一个文件名的字符串
     */
    public static ArrayList getTxtFileNameFromFolder(String filePath) {
        ArrayList fileNames = new ArrayList();
        File file = new File(filePath);
        File[] tempFile = file.listFiles();
        for (int i = 0; i < tempFile.length; i++) {
            if (tempFile[i].isFile())
                if (tempFile[i].getName().indexOf("TXT") != -1 || tempFile[i].getName().indexOf("txt") != -1) {
                    fileNames.add(tempFile[i].getName());
                }
        }
        return fileNames;
    }

    /**
     * 得到图片文件夹集合
     */
    public static List<ImgFolderBean> getImageFolders(Context context) {
        List<ImgFolderBean> folders = new ArrayList<ImgFolderBean>();
        ContentResolver mContentResolver = context.getContentResolver();
        // 扫描图片
        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Images.Media.MIME_TYPE + "= ? or " + MediaStore.Images.Media.MIME_TYPE + "= ?",
                    new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
            List<String> mDirs = new ArrayList<String>();//用于保存已经添加过的文件夹目录
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));// 路径
                File parentFile = new File(path).getParentFile();
                if (parentFile == null)
                    continue;

                String dir = parentFile.getAbsolutePath();
                if (mDirs.contains(dir))//如果已经添加过
                    continue;

                mDirs.add(dir);//添加到保存目录的集合中
                ImgFolderBean folderBean = new ImgFolderBean();
                folderBean.setDir(dir);
                folderBean.setFistImgPath(path);
                if (parentFile.list() == null)
                    continue;
                int count = parentFile.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if (filename.endsWith(".jpeg") || filename.endsWith(".jpg") || filename.endsWith(".png")) {
                            return true;
                        }
                        return false;
                    }
                }).length;

                folderBean.setCount(count);
                folders.add(folderBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return folders;
    }

    /**
     * 得到每个相册下图片的集合
     */
    public static List<ImgFolderBean> getImages(Context context) {
        List<ImgFolderBean> folders = new ArrayList<ImgFolderBean>();
        ContentResolver mContentResolver = context.getContentResolver();
        // 扫描图片
        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Images.Media.MIME_TYPE + "= ? or " + MediaStore.Images.Media.MIME_TYPE + "= ?",
                    new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
            List<String> mDirs = new ArrayList<String>();//用于保存已经添加过的文件夹目录
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));// 路径
                File parentFile = new File(path).getParentFile();
                if (parentFile == null)
                    continue;

                String dir = parentFile.getAbsolutePath();
                if (mDirs.contains(dir))//如果已经添加过
                    continue;
                File dirFile = FileUtils.getFileByPath(dir);
                try {
                    String[] dirFiles = dirFile.list();
                    if (dirFiles != null) {
                        for (String fs : dirFiles) {
                            ImgFolderBean bean = new ImgFolderBean();
                            String filePath = dir + "/" + fs;
                            bean.setName(FileUtils.getFileName(filePath));
                            bean.setCount(FileUtils.getFileLines(filePath));
                            bean.setDir(filePath);

                            bean.setFistImgPath(filePath);
                            folders.add(bean);


                            bean.setSecondImgPath(filePath);

                            bean.setThirthImgPath(filePath);
                            bean.setFourthImgPath(filePath);
                            folders.add(bean);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("0", "e" + e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return folders;
    }

    /**
     * 获得某一文件夹下的所有xml，XML文件名的集合
     *
     * @param filePath 文件夹路径
     * @return ArrayList，其中的每个元素是一个文件名的字符串
     */
    public static ArrayList getXmlFileNameFromFolder(String filePath) {
        ArrayList fileNames = new ArrayList();
        File file = new File(filePath);
        File[] tempFile = file.listFiles();
        for (int i = 0; i < tempFile.length; i++) {
            if (tempFile[i].isFile())
                if (tempFile[i].getName().indexOf("XML") != -1 || tempFile[i].getName().indexOf("xml") != -1) {
                    fileNames.add(tempFile[i].getName());
                }
        }
        return fileNames;
    }

    /**
     * 重命名
     *
     * @param oldName
     * @param newName
     * @return
     */
    public static boolean reNamePath(String oldName, String newName) {
        File f = new File(oldName);
        return f.renameTo(new File(newName));
    }

    /**
     * 按文件名排序
     *
     * @param filePath
     */
    public static ArrayList<String> orderByName(String filePath) {
        ArrayList<String> FileNameList = new ArrayList<String>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        List fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (File file1 : files) {
            if (file1.isDirectory()) {
                FileNameList.add(file1.getName());
            }
        }
        return FileNameList;
    }

    /**
     * 按文件修改时间排序
     *
     * @param filePath
     */
    public static ArrayList<String> orderByDate(String filePath) {
        ArrayList<String> FileNameList = new ArrayList<String>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;// 如果 if 中修改为 返回-1 同时此处修改为返回 1 排序就会是递减
            }

            public boolean equals(Object obj) {
                return true;
            }

        });

        for (File file1 : files) {
            if (file1.isDirectory()) {
                FileNameList.add(file1.getName());
            }
        }
        return FileNameList;
    }

    /**
     * 按文件大小排序
     *
     * @param filePath
     */
    public static ArrayList<String> orderBySize(String filePath) {
        ArrayList<String> FileNameList = new ArrayList<String>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long s1 = getFolderSize(f1);
                long s2 = getFolderSize(f2);

                long diff = s1 - s2;
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;// 如果 if 中修改为 返回-1 同时此处修改为返回 1 排序就会是递减
            }

            public boolean equals(Object obj) {
                return true;
            }
        });

        for (File file1 : files) {
            if (file1.isDirectory()) {
                FileNameList.add(file1.getName());
            }
        }
        return FileNameList;
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file) {

        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 获取本机音乐列表
     *
     * @return
     */
    public static List<Music> getMusics() {
        ArrayList<Music> musics = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径

                if (!new File(path).exists()) {
                    continue;
                }

                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
                String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
                int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时长
                int time = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));// 歌曲的id
                // int albumId = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                Music music = new Music(name, path, album, artist, size, duration);
                musics.add(music);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return musics;
    }

    /**
     * 获取本机视频列表
     *
     * @return
     */
    public static List<Video> getVideos() {

        List<Video> videos = new ArrayList<Video>();

        Cursor c = null;
        try {
            // String[] mediaColumns = { "_id", "_data", "_display_name",
            // "_size", "date_modified", "duration", "resolution" };
            c = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
                if (!new File(path).exists()) {
                    continue;
                }

                int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));// 视频的id
                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 视频名称
                String resolution = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)); //分辨率
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));// 大小
                long duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));// 时长
                long date = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));//修改时间

                Video video = new Video(id, path, name, resolution, size, date, duration);
                videos.add(video);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return videos;
    }

    private static Context mContext;
    private static ContentResolver mContentResolver;

    // 获取视频缩略图
    public Bitmap getVideoThumbnail(int id) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, options);
        return bitmap;
    }

    /**
     * 根据歌名查看音乐
     *
     * @param context 上下文
     * @param key     关键字
     * @return
     */
    public static String[] queryMusic(Context context, String key) {
        ArrayList<String> nameList = new ArrayList<>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Audio.Media.DISPLAY_NAME + " LIKE '%" + key + "%'",
                    null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径

                if (!FileManager.isExists(path)) {
                    continue;
                }

                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                nameList.add(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        if (nameList.isEmpty()) {
            return new String[]{};
        }
        return (String[]) nameList.toArray(new String[nameList.size()]);
    }

    /**
     * 根据照片名查看照片
     *
     * @param context 上下文
     * @param key     关键字
     * @return
     */
    public static String[] queryPhoto(Context context, String key) {
        ArrayList<String> nameList = new ArrayList<>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Images.Media.DISPLAY_NAME + " LIKE '%" + key + "%'",
                    null,
                    MediaStore.Images.Media.DEFAULT_SORT_ORDER);

            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));// 路径

                if (!FileManager.isExists(path)) {
                    continue;
                }

                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)); // 歌曲名
                nameList.add(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        if (nameList.isEmpty()) {
            return new String[]{};
        }
        return (String[]) nameList.toArray(new String[nameList.size()]);
    }

    /**
     * 根据视频名查看视频
     *
     * @param context 上下文
     * @param key     关键字
     * @return
     */
    public static String[] quertVideo(Context context, String key) {
        ArrayList<String> nameList = new ArrayList<>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Video.Media.DISPLAY_NAME + " LIKE '%" + key + "%'",
                    null,
                    MediaStore.Video.Media.DEFAULT_SORT_ORDER);

            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径

                if (!FileManager.isExists(path)) {
                    continue;
                }

                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 歌曲名
                nameList.add(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        if (nameList.isEmpty()) {
            return new String[]{};
        }
        return (String[]) nameList.toArray(new String[nameList.size()]);
    }

    /**
     * 根据文件名查看文件
     *
     * @param context 上下文
     * @param key     关键字
     * @return
     */
    public static String[] quertFile(Context context, String key) {
        ArrayList<String> nameList = new ArrayList<>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), new String[]{"_id", "_data", "_size"}, key, null, MediaStore.Files.FileColumns.MEDIA_TYPE);

            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));// 路径

                if (!FileManager.isExists(path)) {
                    continue;
                }

                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)); // 歌曲名
                nameList.add(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        if (nameList.isEmpty()) {
            return new String[]{};
        }
        return (String[]) nameList.toArray(new String[nameList.size()]);
    }

    /**
     * 通过文件类型得到相应文件的集合
     **/
    public static List<FileBean> getFilesByType(int fileType) {
        List<FileBean> files = new ArrayList<FileBean>();
        // 扫描files文件库
        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Files.getContentUri("external"), new String[]{"_id", "_data", "_size"}, null, null, null);
            int dataindex = c.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            int sizeindex = c.getColumnIndex(MediaStore.Files.FileColumns.SIZE);

            while (c.moveToNext()) {
                String path = c.getString(dataindex);

                if (getFileType(path) == fileType) {
                    if (!isExists(path)) {
                        continue;
                    }
                    long size = c.getLong(sizeindex);
                    FileBean fileBean = new FileBean(path, getFileIconByPath(path));
                    files.add(fileBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return files;
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件的路径
     * @return
     */
    public static boolean isExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 文档类型
     */
    public static final int TYPE_DOC = 0;
    /**
     * apk类型
     */
    public static final int TYPE_APK = 1;
    /**
     * 压缩包类型
     */
    public static final int TYPE_ZIP = 2;

    public static int getFileType(String path) {
        path = path.toLowerCase();
        if (path.endsWith(".doc") || path.endsWith(".docx") || path.endsWith(".xls") || path.endsWith(".xlsx")
                || path.endsWith(".ppt") || path.endsWith(".pptx")) {
            return TYPE_DOC;
        } else if (path.endsWith(".apk")) {
            return TYPE_APK;
        } else if (path.endsWith(".zip") || path.endsWith(".rar") || path.endsWith(".tar") || path.endsWith(".gz")) {
            return TYPE_ZIP;
        } else {
            return -1;
        }
    }

    /**
     * 通过文件名获取文件图标
     */
    public static int getFileIconByPath(String path) {
        path = path.toLowerCase();
        int iconId = R.drawable.ic_unknow;
        if (path.endsWith(".txt")) {
            iconId = R.drawable.ic_doc;
        } else if (path.endsWith(".doc") || path.endsWith(".docx")) {
            iconId = R.drawable.ic_doc;
        } else if (path.endsWith(".xls") || path.endsWith(".xlsx")) {
            iconId = R.drawable.ic_doc;
        } else if (path.endsWith(".ppt") || path.endsWith(".pptx")) {
            iconId = R.drawable.ic_doc;
        } else if (path.endsWith(".xml")) {
            iconId = R.drawable.ic_doc;
        } else if (path.endsWith(".htm") || path.endsWith(".html")) {
            iconId = R.drawable.ic_doc;
        }
        return iconId;
    }


    /**
     * 通过图片文件夹的路径获取该目录下的图片
     */
    public List<String> getImgListByDir(String dir) {
        ArrayList<String> imgPaths = new ArrayList<>();
        File directory = new File(dir);
        if (directory == null || !directory.exists()) {
            return imgPaths;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            String path = file.getAbsolutePath();
            if (isPicFile(path)) {
                imgPaths.add(path);
            }
        }
        return imgPaths;
    }

    /**
     * 是否是图片文件
     */
    public static boolean isPicFile(String path) {
        path = path.toLowerCase();
        if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")) {
            return true;
        }
        return false;
    }

    /**
     * 获取已安装apk的列表
     */
    public static List<AppInfo> getAppInfos(Context context) {

        ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
        //获取到包的管理者
        PackageManager packageManager = context.getPackageManager();
        //获得所有的安装包
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

        //遍历每个安装包，获取对应的信息
        for (PackageInfo packageInfo : installedPackages) {

            AppInfo appInfo = new AppInfo();

            appInfo.setApplicationInfo(packageInfo.applicationInfo);
            appInfo.setVersionCode(packageInfo.versionCode);

            //得到icon
            Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(drawable);

            //得到程序的名字
            String apkName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setApkName(apkName);

            //得到程序的包名
            String packageName = packageInfo.packageName;
            appInfo.setApkPackageName(packageName);

            //得到程序的资源文件夹
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            //得到apk的大小
            long size = file.length();
            appInfo.setApkSize(size);

            System.out.println("---------------------------");
            System.out.println("程序的名字:" + apkName);
            System.out.println("程序的包名:" + packageName);
            System.out.println("程序的大小:" + size);


            //获取到安装应用程序的标记
            int flags = packageInfo.applicationInfo.flags;

            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //表示系统app
                appInfo.setIsUserApp(false);
            } else {
                //表示用户app
                appInfo.setIsUserApp(true);
            }

            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                //表示在sd卡
                appInfo.setIsRom(false);
            } else {
                //表示内存
                appInfo.setIsRom(true);
            }


            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
