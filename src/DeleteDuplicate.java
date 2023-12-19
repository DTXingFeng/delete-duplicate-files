
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DeleteDuplicate {
    ArrayList<File> name = new ArrayList<>();
    ArrayList<String> MD5name = new ArrayList<>();
    ArrayList<Integer> deleteNum = new ArrayList<Integer>();
    private final File file;

    public DeleteDuplicate(String filePath){
        file = new File(filePath);
        toMD5();
        duiBi();
        delete();
    }

    public void toMD5() {
        System.out.println("开始转换md5值");
        for (File f : file.listFiles()){
            if (f.isFile()) {
                name.add(f);
                try {
                    MD5name.add(md5HashCode(new FileInputStream(f)));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void duiBi(){
        System.out.println("开始统计重复文件");
        for (int i = 0;i < name.size();i++){
            for (int x = i+1; x < name.size(); x++){
                if (MD5name.get(i).equals(MD5name.get(x))){
                    deleteNum.add(x);
                }
            }
        }
    }
    public void delete(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("开始删除重复文件");

        // 使用Map来保存重复的文件，键为MD5，值为文件列表
        Map<String, ArrayList<File>> duplicatesMap = new HashMap<>();

        for (int i = 0; i < deleteNum.size(); i++) {
            int index = deleteNum.get(i);
            File duplicateFile = name.get(index);
            String md5 = MD5name.get(index);

            if (!duplicatesMap.containsKey(md5)) {
                duplicatesMap.put(md5, new ArrayList<>());
            }

            duplicatesMap.get(md5).add(duplicateFile);
        }

        // 列出重复文件并确认
        for (Map.Entry<String, ArrayList<File>> entry : duplicatesMap.entrySet()) {
            String md5 = entry.getKey();
            ArrayList<File> duplicateFiles = entry.getValue();

            System.out.println("MD5: " + md5);
            for (File file : duplicateFiles) {
                System.out.println("文件：" + file.getName() + "\t大小：" + file.length() + " 字节");
            }

            System.out.println("是否删除这些文件？ (Y/N): ");
            String response = scanner.nextLine().trim().toUpperCase();

            if ("Y".equals(response)) {
                // 保留一个文件，删除其他重复文件
                for (File fileToDelete : duplicateFiles) {
                    System.out.println("正在删除文件：" + fileToDelete.getName());
                    fileToDelete.delete();
                }
            } else {
                System.out.println("未删除文件：" + duplicateFiles.get(0).getName());
            }
        }

        scanner.close();
    }


    public static String md5HashCode(InputStream fis) {
        try {
            //拿到一个MD5转换器,如果想使用SHA-1或SHA-256，则传入SHA-1,SHA-256
            MessageDigest md = MessageDigest.getInstance("MD5");

            //分多次将一个文件读入
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            fis.close();
            //转换并返回包含16个元素字节数组,返回数值范围为-128到127
            byte[] md5Bytes  = md.digest();
            BigInteger bigInt = new BigInteger(1, md5Bytes);//1代表绝对值
            return bigInt.toString(16);//转换为16进制
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
