package com.tomato.tomato_mall.enums;

/**
 * 对象存储文件类型枚举
 * <p>
 * 定义系统中使用的对象存储服务(OSS)文件分类，每种类型对应一个存储目录
 * 用于文件上传时指定存储路径和权限管理
 * </p>
 * 
 * @author Team CBDDL
 * @version 1.0
 */
public enum OSSFileTypeEnum {

    /**
     * 用户头像文件类型
     * 存储于"avatar"目录下
     */
    AVATAR("avatar"),

    /**
     * 商品封面文件类型
     * 存储于"cover"目录下
     */
    COVER("cover");

    private final String directory;

    /**
     * 构造函数
     * 
     * @param directory 文件类型对应的存储目录名称
     */
    OSSFileTypeEnum(String directory) {
        this.directory = directory;
    }

    /**
     * 获取当前文件类型对应的存储目录名称
     * 
     * @return 存储目录名称
     */
    public String getDirectory() {
        return directory;
    }
    
    /**
     * 通过存储目录名称获取对应的文件类型枚举
     * <p>
     * 查找并返回与指定目录名称匹配的文件类型枚举，忽略大小写
     * </p>
     * 
     * @param directory 要查找的存储目录名称
     * @return 匹配的文件类型枚举，如果未找到则返回null
     */
    public static OSSFileTypeEnum getByDirectory(String directory) {
        for (OSSFileTypeEnum type : values()) {
            if (type.directory.equalsIgnoreCase(directory)) {
                return type;
            }
        }
        return null;
    }
}