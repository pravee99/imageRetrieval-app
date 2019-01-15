
/*
 * Assignment 1 :Content Based Image Retrieval
 @author Praveena Avula
*/

import java.awt.image.BufferedImage;
import java.lang.Object.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class readImage {

  double intensityMatrix[][] = new double[100][25];
  double colorCodeMatrix[][] = new double[100][64];
  double[] imageSize = new double[101];

  /*
   * Each image is retrieved from the file. The intensityMatrix and ColorCode
   * matrix are filled and the contents are written to intensity.txt and
   * colorcode.txt files respectively.
   */
  public readImage() {
    getIntensity();
    getColorCode();
    writeIntensity();
    writeColorCode();

  }

  // intensity method
  public void getIntensity() {
    int imageCount = 1;
    while (imageCount < 101) {
      try {
        File f = new File("images/" + imageCount + ".jpg");
        BufferedImage img = ImageIO.read(f);
        int pix = 0, red = 0, blue = 0, green = 0;
        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();
        double intensity = 0;
        imageSize[imageCount] = imageWidth * imageHeight;

        for (int i = 0; i < imageWidth; i++) {
          for (int j = 0; j < imageHeight; j++) {
            pix = img.getRGB(i, j);
            red = (pix >> 16) & 0xff;
            green = (pix >> 8) & 0xff;
            blue = (pix) & 0xff;
            intensity = 0.299 * red + 0.587 * green + 0.114 * blue;

            if (intensity >= 250) {
              intensityMatrix[imageCount - 1][(int) ((intensity / 10) - 1)]++;
            } else {
              intensityMatrix[imageCount - 1][(int) (intensity / 10)]++;
            }

          }
        }
      } catch (IOException e) {
        System.out.println("Error occurred when reading the file.");
      }
      imageCount++;
    }

  }

  // color code method
  public void getColorCode() {
    int imageCount = 1;

    while (imageCount < 101) {
      try {
        File f = new File("images/" + imageCount + ".jpg");
        BufferedImage img = ImageIO.read(f);
        int pix = 0, red = 0, blue = 0, green = 0;
        int colorCodeBit = 0;
        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();
        imageSize[imageCount] = imageWidth * imageHeight;

        for (int i = 0; i < imageWidth; i++) {
          for (int j = 0; j < imageHeight; j++) {
            pix = img.getRGB(i, j);
            red = (pix >> 16) & 0xff;
            green = (pix >> 8) & 0xff;
            blue = (pix) & 0xff;
            red = ((red >> 2)) & 0xf0;
            green = ((green >> 4)) & 0x0c;
            blue = (blue >> 6);
            colorCodeBit = red + green + blue;

            colorCodeMatrix[imageCount - 1][colorCodeBit]++;
          }
        }

      } catch (IOException e) {
        System.out.println("Error occurred when reading the file.");
      }
      imageCount++;
    }
  }

  // This method writes the contents of the colorCode matrix to a file named
  // colorCodes.txt.
  public void writeColorCode() {
    writeMatrix("colorCode.txt", colorCodeMatrix);
  }

  // This method writes the contents of the intensity matrix to a file called
  // intensity.txt
  public void writeIntensity() {
    writeMatrix("intensity.txt", intensityMatrix);
  }

  void writeMatrix(String filename, double[][] matrix) {
    try {
      BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
      for (int i = 0; i < matrix.length; i++) {
        for (int j = 0; j < matrix[i].length; j++) {
          bufferedWriter.write(matrix[i][j] + ",");
        }
        bufferedWriter.newLine();
      }
      bufferedWriter.flush();
    } catch (IOException e) {
    }
  }

  public static void main(String[] args) {
    new readImage();
  }

}
