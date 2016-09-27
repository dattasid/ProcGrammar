package sid.example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class T
{

    public static void main(String[] args) throws IOException
    {
        BufferedImage i = ImageIO.read(new File("D:/videos/main.jpg"));
        
        int w = i.getWidth();
        int h = i.getHeight();
        
        BufferedImage i1 = i.getSubimage(0, 0, w/3, h);
        BufferedImage i2 = i.getSubimage(w/3, 0, w/3, h);
        BufferedImage i3 = i.getSubimage(2*w/3, 0, w/3, h);
        
        ImageIO.write(i1, "jpg", new File("d:/videos/im1.jpg"));
        ImageIO.write(i2, "jpg", new File("d:/videos/im2.jpg"));
        ImageIO.write(i3, "jpg", new File("d:/videos/im3.jpg"));
        
        System.out.println(w+" "+h);
    }
}
