package seekbar.ggh.com.utils.bean;

public class ImgFolderBean {
    /**当前文件夹的路径*/
    private String dir;
    /**第一张图片的路径，用于做文件夹的封面图片*/
    private String fistImgPath;
    private String secondImgPath;
    private String thirthImgPath;
    private String fourthImgPath;
    /**文件夹名*/
    private String name;
    /**文件夹中图片的数量*/
    private int count;
    /**是否是文件*/
    private boolean isFile;


    public String getSecondImgPath() {
        return secondImgPath;
    }

    public void setSecondImgPath(String secondImgPath) {
        this.secondImgPath = secondImgPath;
    }

    public String getThirthImgPath() {
        return thirthImgPath;
    }

    public void setThirthImgPath(String thirthImgPath) {
        this.thirthImgPath = thirthImgPath;
    }

    public String getFourthImgPath() {
        return fourthImgPath;
    }

    public void setFourthImgPath(String fourthImgPath) {
        this.fourthImgPath = fourthImgPath;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFistImgPath() {
        return fistImgPath;
    }

    public void setFistImgPath(String fistImgPath) {
        this.fistImgPath = fistImgPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
