
/* 
Assignment 1 :Content Based Image Retrieval
 @author Praveena Avula
 @version 2.0
 @Date 10/26
*/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicToolBarUI;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.*;

public class CBIR extends JFrame {

  private JLabel photographLabel = new JLabel(); // container to hold a large
  private JButton[] button; // creates an array of JButtons
  private int[] buttonOrder = new int[101]; // creates an array to keep up with the image order
  private double[] imageSize = new double[101]; // keeps up with the image sizes
  private GridLayout gridLayout1;
  private JButton intensity;
  private JButton colorCode;
  private JButton relevance;
  private JButton previousPage;
  private JButton nextPage;
  private JPanel sidePanel;
  private JMenuBar menubar;
  private JToolBar vertical;
  private double[][] intensityMatrix = new double[100][25];
  private double[][] colorCodeMatrix = new double[100][64];
  private Map<Double, List<Integer>> map;
  private Map<Double, List<Integer>> distanceMap;
  private JCheckBox checkBox;
  List<Integer> selectedImages;
  Map<Integer, JCheckBox> checkBoxes = new HashMap<Integer, JCheckBox>();;
  int picNo = 0;
  int imageCount = 1; // keeps up with the number of images displayed since the first page.
  int pageNo = 1;

  public static void main(String args[]) {

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        CBIR app = new CBIR();
        app.setVisible(true);
      }
    });
  }

  public CBIR() {
    // The following lines set up the interface including the layout of the buttons
    // and JPanels.

    menubar = new JMenuBar();
    JMenu file = new JMenu("Please select an image from the below to proceed..");
    menubar.add(file);
    setJMenuBar(menubar);
    JPanel bottomPanel = new JPanel();
    gridLayout1 = new GridLayout(5, 4, 0, 0);
    sidePanel = new JPanel();
    sidePanel.setLayout(gridLayout1);
    vertical = new JToolBar(JToolBar.VERTICAL) {
      private static final long serialVersionUID = 1L;

      @Override
      public void paintComponent(Graphics g) {
        Dimension size = vertical.getSize();
        g.drawImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/backGroundImage.png")), 0, 0,
            size.width, size.height, this);

      }
    };

    vertical.setFloatable(false);
    vertical.setOpaque(false);
    vertical.setMargin(new Insets(10, 5, 5, 5));

    photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
    photographLabel.setHorizontalTextPosition(JLabel.CENTER);
    photographLabel.setHorizontalAlignment(JLabel.CENTER);
    photographLabel.setBorder(BorderFactory.createLineBorder(Color.black, 3));

    ImageIcon intensityIcon = new ImageIcon(getClass().getResource("images/" + "intensity.png"));
    intensity = new JButton();
    Image intensityImage = intensityIcon.getImage();
    Image newIntensityImage = intensityImage.getScaledInstance(150, 60, java.awt.Image.SCALE_SMOOTH);
    intensity.setIcon(new ImageIcon(newIntensityImage));
    intensity.setBorder(BorderFactory.createEtchedBorder(Color.RED, Color.BLACK));

    colorCode = new JButton();
    ImageIcon ColorCodeIcon = new ImageIcon(getClass().getResource("images/" + "colorCode.jpg"));
    Image colorCodeImage = ColorCodeIcon.getImage();
    Image newColorCodeimage = colorCodeImage.getScaledInstance(150, 60, java.awt.Image.SCALE_SMOOTH);
    colorCode.setIcon(new ImageIcon(newColorCodeimage));
    colorCode.setBorder(BorderFactory.createEtchedBorder(Color.RED, Color.BLACK));

    relevance = new JButton();
    ImageIcon relevanceIcon = new ImageIcon(getClass().getResource("images/" + "relevance.jpeg"));
    Image relevanceImage = relevanceIcon.getImage();
    Image newRelevanceImage = relevanceImage.getScaledInstance(150, 60, java.awt.Image.SCALE_SMOOTH);
    relevance.setIcon(new ImageIcon(newRelevanceImage));
    relevance.setBorder(BorderFactory.createEtchedBorder(Color.RED, Color.BLACK));

    checkBox = new JCheckBox("Relevance");

    vertical.add(photographLabel);
    vertical.addSeparator(new Dimension(1, 50));
    vertical.add(intensity);
    vertical.addSeparator(new Dimension(1, 5));
    vertical.add(colorCode);
    vertical.addSeparator(new Dimension(1, 5));
    vertical.add(relevance);
    vertical.add(checkBox);

    add(vertical, BorderLayout.WEST);
    add(sidePanel, BorderLayout.CENTER);

    ImageIcon previousIcon = new ImageIcon(getClass().getResource("images/" + "previousPage.png"));
    previousPage = new JButton();
    Image previousImage = previousIcon.getImage();
    Image newPreviousImage = previousImage.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
    previousPage.setIcon(new ImageIcon(newPreviousImage));

    ImageIcon nextIcon = new ImageIcon(getClass().getResource("images/" + "nextPage.png"));
    nextPage = new JButton();
    Image nextImage = nextIcon.getImage();
    Image newNextImage = nextImage.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
    nextPage.setIcon(new ImageIcon(newNextImage));

    bottomPanel.add(previousPage);
    bottomPanel.add(nextPage);
    add(bottomPanel, BorderLayout.SOUTH);

    setTitle("CONTENT BASED IMAGE RETRIEVAL");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    nextPage.addActionListener(new nextPageHandler());
    previousPage.addActionListener(new previousPageHandler());
    intensity.addActionListener(new intensityHandler());
    colorCode.addActionListener(new colorCodeHandler());
    relevance.addActionListener(new relevanceHandler());
    checkBox.addActionListener(new checkBoxHandler());
    // setSize(1100, 750);
    setSize(2560, 1600);
    // this centers the frame on the screen
    setLocationRelativeTo(null);

    button = new JButton[101];
    /*
     * This for loop goes through the images in the database and stores them as
     * icons and adds the images to JButtons and then to the JButton array
     */
    for (int i = 1; i < 101; i++) {
      ImageIcon icon;
      icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));

      if (icon != null) {
        if (i == 1)
          photographLabel.setIcon(icon);
        button[i] = new JButton();
        Image actualImage = icon.getImage();
        Image scaledImage = actualImage.getScaledInstance(200, 120, java.awt.Image.SCALE_SMOOTH);
        button[i].setIcon(new ImageIcon(scaledImage));
        button[i].addActionListener(new IconButtonHandler(i, icon));
        buttonOrder[i] = i;
      }
    }

    // Constructor of readImage Class calls the necessary methods for writing
    // contents into intensity.txt and colorcode.txt files
    readImage readImage = new readImage();
    imageSize = readImage.imageSize;
    displayFirstPage();
  }

  /*
   * This method opens the intensity text file containing the intensity matrix
   * with the histogram bin values for each image. The contents of the matrix are
   * processed and stored in a two dimensional array called intensityMatrix.
   */
  public void readIntensityFile() {
    int row = 0, column = 0;

    try {
      BufferedReader input = new BufferedReader(new FileReader("intensity.txt"));
      String line;

      while ((line = input.readLine()) != null) {
        StringTokenizer token = new StringTokenizer(line, ",");
        column = 0;
        while (token.hasMoreTokens()) {
          intensityMatrix[row][column] = Double.parseDouble(token.nextToken());
          column++;
        }
        row++;

      }
      input.close();
    } catch (IOException e) {
      System.out.println("File Read Error");
    }

  }

  /*
   * This method opens the color code text file containing the color code matrix
   * with the histogram bin values for each image. The contents of the matrix are
   * processed and stored in a two dimensional array called colorCodeMatrix.
   */
  private void readColorCodeFile() {
    int row = 0, column = 0;

    try {
      BufferedReader input = new BufferedReader(new FileReader("colorCode.txt"));
      String line;

      while ((line = input.readLine()) != null) {
        column = 0;
        StringTokenizer token = new StringTokenizer(line, ",");
        while (token.hasMoreTokens()) {
          colorCodeMatrix[row][column] = Double.parseDouble(token.nextToken());
          column++;
        }
        row++;

      }
      input.close();
    } catch (IOException e) {
      System.out.println("File Read Error");
    }

  }

  /*
   * This method displays the first twenty images in the panelBottom. The for loop
   * starts at number one and gets the image number stored in the buttonOrder
   * array and assigns the value to imageButNo. The button associated with the
   * image is then added to panelBottom1. The for loop continues this process
   * until twenty images are displayed in the panelBottom1
   */

  public void displayFirstPage() {
    int imageButNo = 0;
    imageCount = 1;
    sidePanel.removeAll();
    JPanel test;

    for (int i = 1; i < 21; i++) {

      imageButNo = buttonOrder[i];
      if (checkBox.isSelected()) {
        test = new JPanel();

        checkBoxes.put(imageButNo, new JCheckBox());
        if (selectedImages != null) {
          if (selectedImages.contains(imageButNo - 1)) {
            checkBoxes.get(imageButNo).setSelected(true);
          }

        }
        test.add(button[imageButNo]);
        test.add(checkBoxes.get(imageButNo));
        sidePanel.add(test);

      } else {
        sidePanel.add(button[imageButNo]);
      }
      imageCount++;
    }

    previousPage.setEnabled(false);
    sidePanel.revalidate();
    sidePanel.repaint();

  }

  /*
   * This class implements an ActionListener for each iconButton. When an icon
   * button is clicked, the image on the the button is added to the
   * photographLabel and the picNo is set to the image number selected and being
   * displayed.
   */
  private class IconButtonHandler implements ActionListener {
    int pNo = 0;
    ImageIcon iconUsed;

    IconButtonHandler(int i, ImageIcon j) {
      pNo = i;
      iconUsed = j; // sets the icon to the one used in the button
    }

    public void actionPerformed(ActionEvent e) {
      photographLabel.setIcon(iconUsed);
      picNo = pNo;
    }

  }

  /*
   * This class implements an ActionListener for the nextPageButton. The last
   * image number to be displayed is set to the current image count plus 20. If
   * the endImage number equals 101, then the next page button does not display
   * any new images because there are only 100 images to be displayed. The first
   * picture on the next page is the image located in the buttonOrder array at the
   * imageCount
   */
  private class nextPageHandler implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      previousPage.setEnabled(true);
      int imageButNo = 0;
      int endImage = imageCount + 20;

      if (endImage <= 101) {
        sidePanel.removeAll();
        for (int i = imageCount; i < endImage; i++) {
          imageButNo = buttonOrder[i];
          sidePanel.add(button[imageButNo]);
          imageCount++;

        }
        if (endImage == 101)
          nextPage.setEnabled(false);
        sidePanel.revalidate();
        sidePanel.repaint();
      }
    }

  }

  /*
   * This class implements an ActionListener for the previousPageButton. The last
   * image number to be displayed is set to the current image count minus 40. If
   * the endImage number is less than 1, then the previous page button does not
   * display any new images because the starting image is 1. The first picture on
   * the next page is the image located in the buttonOrder array at the imageCount
   */
  private class previousPageHandler implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      int imageButNo = 0;
      int startImage = imageCount - 40;
      int endImage = imageCount - 20;

      if (endImage <= 101)
        nextPage.setEnabled(true);
      if (startImage == 1)
        previousPage.setEnabled(false);

      if (checkBox.isSelected() && startImage == 1) {
        if (startImage == 1) {

          JPanel test;
          sidePanel.removeAll();

          for (int i = startImage; i < endImage; i++) {
            imageButNo = buttonOrder[i];
            test = new JPanel();
            checkBoxes.put(imageButNo, new JCheckBox());
            test.add(button[imageButNo]);
            test.add(checkBoxes.get(imageButNo));
            sidePanel.add(test);

            imageCount--;

          }

          sidePanel.revalidate();
          sidePanel.repaint();

        }
      } else {
        if (startImage >= 1) {
          sidePanel.removeAll();
          /*
           * The for loop goes through the buttonOrder array starting with the startImage
           * value and retrieves the image at that place and then adds the button to the
           * panelBottom1.
           */

          for (int i = startImage; i < endImage; i++) {
            imageButNo = buttonOrder[i];

            sidePanel.add(button[imageButNo]);
            imageCount--;

          }

          sidePanel.revalidate();
          sidePanel.repaint();
        }
      }

    }

  }
  /*
   * This class implements an ActionListener when the user selects the
   * intensityHandler button. The image number that the user would like to find
   * similar images for is stored in the variable pic. pic takes the image number
   * associated with the image selected and subtracts one to account for the fact
   * that the colorCode starts with zero and not one. The size of the image is
   * retrieved from the imageSize array. The selected image's colorCode bin values
   * are compared to all the other image's colorCode bin values and a score is
   * determined for how well the images compare. The images are then arranged from
   * most similar to the least.
   */

  private class colorCodeHandler implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      readColorCodeFile();
      map = new TreeMap<Double, List<Integer>>();
      int compareImage = 0;
      int pic = (picNo - 1);

      double picSize = imageSize[picNo];
      double[] selectedImageBin = colorCodeMatrix[pic];

      while (compareImage < 100) {
        double sum = 0;

        for (int i = 0; i < 64; i++) {

          sum += Math
              .abs(((colorCodeMatrix[compareImage][i]) / imageSize[compareImage + 1]) - selectedImageBin[i] / picSize);

        }

        List<Integer> listOfPics = null;
        if (map.get(sum) == null) {
          listOfPics = new ArrayList<Integer>();
          listOfPics.add(compareImage + 1);
          map.put(sum, listOfPics);
        } else {
          listOfPics = map.get(sum);
          listOfPics.add(compareImage + 1);
          map.put(sum, listOfPics);
        }

        compareImage++;
      }
      int order = 1;
      for (Map.Entry<Double, List<Integer>> entry : map.entrySet()) {

        for (int i = 0; i < entry.getValue().size(); i++) {

          buttonOrder[order] = entry.getValue().get(i);
          order++;
        }

      }
      displayFirstPage();
    }
  }

  /*
   * This class implements an ActionListener when the user selects the
   * intensityHandler button. The image number that the user would like to find
   * similar images for is stored in the variable pic. pic takes the image number
   * associated with the image selected and subtracts one to account for the fact
   * that the intensityMatrix starts with zero and not one. The size of the image
   * is retrieved from the imageSize array. The selected image's intensity bin
   * values are compared to all the other image's intensity bin values and a score
   * is determined for how well the images compare. The images are then arranged
   * from most similar to the least.
   */
  private class intensityHandler implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      readIntensityFile();
      map = new TreeMap<Double, List<Integer>>();
      int compareImage = 0;
      int pic = (picNo - 1);
      double picSize = imageSize[picNo];
      double[] selectedImageBin = intensityMatrix[pic];

      while (compareImage < 100) {
        double sum = 0;

        for (int i = 0; i < 25; i++) {

          sum += Math
              .abs(((intensityMatrix[compareImage][i]) / imageSize[compareImage + 1]) - selectedImageBin[i] / picSize);

        }

        List<Integer> listOfPics = null;
        if (map.get(sum) == null) {
          listOfPics = new ArrayList<Integer>();
          listOfPics.add(compareImage + 1);
          map.put(sum, listOfPics);
        }

        else {
          listOfPics = map.get(sum);
          listOfPics.add(compareImage + 1);
          map.put(sum, listOfPics);
        }

        compareImage++;
      }
      int order = 1;
      for (Map.Entry<Double, List<Integer>> entry : map.entrySet()) {

        for (int i = 0; i < entry.getValue().size(); i++) {

          buttonOrder[order] = entry.getValue().get(i);
          order++;
        }

      }

      displayFirstPage();

    }

  }

  /**
   * This method implements the relevance feedback mechanism by considering the
   * user selected images for each iteration and returns relevant images
   */
  private class relevanceHandler implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      int pic = (picNo - 1);
      if (checkBoxes.size() > 0) {
        selectedImages = new ArrayList<Integer>();
        for (Map.Entry<Integer, JCheckBox> map : checkBoxes.entrySet()) {
          if (map.getValue().isSelected()) {
            selectedImages.add(map.getKey() - 1);
          }
        }
        double[] newStandardDeviation = new double[89];
        double[] newMean = new double[89];
        double[] updatedWeight = new double[89];
        double sum;
        double[][] normalizedMatrix = createNormalizedMatrix();

        for (int j = 0; j < 89; j++) {
          sum = 0;
          for (int i : selectedImages) {
            sum += normalizedMatrix[i][j];
          }
          newMean[j] = sum / selectedImages.size();

        }

        double stdSum;
        for (int j = 0; j < 89; j++) {
          stdSum = 0;
          for (int i : selectedImages) {
            stdSum += Math.pow(normalizedMatrix[i][j] - newMean[j], 2);
          }
          newStandardDeviation[j] = Math.sqrt(stdSum / (selectedImages.size() - 1));

        }
        // Calculating the minimum of all standard deviations of selected images
        double min = Double.MAX_VALUE;
        for (int i = 0; i < 89; i++) {
          if (newStandardDeviation[i] != 0 && min > newStandardDeviation[i]) {
            min = newStandardDeviation[i];

          }
        }
        // Calculating updated weights for each factor
        for (int j = 0; j < 89; j++) {
          if (newMean[j] == 0 && newStandardDeviation[j] == 0)
            updatedWeight[j] = 0;
          else if (newMean[j] != 0 && newStandardDeviation[j] == 0) {
            newStandardDeviation[j] = 0.5 * min;
            updatedWeight[j] = 1 / newStandardDeviation[j];

          } else
            updatedWeight[j] = 1 / newStandardDeviation[j];
        }

        double sumOfWieghts = 0;
        for (int j = 0; j < 89; j++) {
          sumOfWieghts += updatedWeight[j];
        }
        // Normalizing the updated weights
        for (int j = 0; j < 89; j++) {
          updatedWeight[j] = updatedWeight[j] / sumOfWieghts;
        }

        distanceMap = new TreeMap<Double, List<Integer>>();
        calculateDistanceMap(normalizedMatrix, pic, distanceMap, updatedWeight);
        computeImageOrder(distanceMap);

      } else {
        double[][] normalizedMatrix = createNormalizedMatrix();

        distanceMap = new TreeMap<Double, List<Integer>>();
        calculateDistanceMap(normalizedMatrix, pic, distanceMap);

        computeImageOrder(distanceMap);
      }
      displayFirstPage();
    }

    /**
     * This method computes the image's order based on the entries in distanceMap
     * 
     * @param distanceMap
     */
    private void computeImageOrder(Map<Double, List<Integer>> distanceMap) {
      int order = 1;
      for (Map.Entry<Double, List<Integer>> entry : distanceMap.entrySet()) {

        for (int i = 0; i < entry.getValue().size(); i++) {

          buttonOrder[order] = entry.getValue().get(i);
          order++;
        }

      }
    }

    /**
     * This method calculates the distance between images in Iteration 0 when all
     * the weights are same
     * 
     * @param normalizedMatrix
     * @param pic
     * @param distanceMap
     */
    private void calculateDistanceMap(double[][] normalizedMatrix, int pic, Map<Double, List<Integer>> distanceMap) {
      int compareImage = 0;
      while (compareImage < 100) {
        double currentSum = 0;

        for (int i = 0; i < 89; i++) {

          currentSum += Math.abs(normalizedMatrix[compareImage][i] - normalizedMatrix[pic][i]);
        }
        List<Integer> listOfPics = null;
        if (distanceMap.get(currentSum) == null) {
          listOfPics = new ArrayList<Integer>();
          listOfPics.add(compareImage + 1);
          distanceMap.put(currentSum, listOfPics);
        } else {
          listOfPics = distanceMap.get(currentSum);
          listOfPics.add(compareImage + 1);
          distanceMap.put(currentSum, listOfPics);
        }

        compareImage++;
      }
      return;
    }

    /**
     * This method calculates the distance between images based on different factor
     * weights
     * 
     * @param normalizedMatrix
     * @param pic
     * @param distanceMap
     * @param updatedWeight
     */
    private void calculateDistanceMap(double[][] normalizedMatrix, int pic, Map<Double, List<Integer>> distanceMap,
        double[] updatedWeight) {
      int compareImage = 0;

      while (compareImage < 100) {
        double currentSum = 0;

        for (int i = 0; i < 89; i++) {

          currentSum = currentSum
              + (updatedWeight[i] * (Math.abs(normalizedMatrix[compareImage][i] - normalizedMatrix[pic][i])));
        }

        List<Integer> listOfPics = null;
        if (distanceMap.get(currentSum) == null) {
          listOfPics = new ArrayList<Integer>();
          listOfPics.add(compareImage + 1);
          distanceMap.put(currentSum, listOfPics);
        } else {
          listOfPics = distanceMap.get(currentSum);
          listOfPics.add(compareImage + 1);
          distanceMap.put(currentSum, listOfPics);
        }

        compareImage++;
      }
      return;
    }

    /**
     * This method creates the normalized matrix by combining intensity and colour
     * code bins
     * 
     * @return
     */

    private double[][] createNormalizedMatrix() {
      readIntensityFile();
      readColorCodeFile();
      double[][] newMatrix = new double[100][89];

      for (int i = 0; i < intensityMatrix.length; i++) {
        double currentImageSize = imageSize[i + 1];
        for (int j = 0; j < intensityMatrix[i].length; j++) {
          intensityMatrix[i][j] = intensityMatrix[i][j] / currentImageSize;
          newMatrix[i][j] = intensityMatrix[i][j];
        }
      }
      int k;
      for (int i = 0; i < colorCodeMatrix.length; i++) {
        double currentImageSize = imageSize[i + 1];
        k = 25;
        for (int j = 0; j < colorCodeMatrix[i].length; j++) {
          colorCodeMatrix[i][j] = colorCodeMatrix[i][j] / currentImageSize;
          newMatrix[i][k] = colorCodeMatrix[i][j];

          k++;
        }
      }

      double[][] normalizedMatrix = new double[100][89];
      double[] mean = new double[89];
      double[] standardDeviation = new double[89];
      double featureMean, sum;
      /*
       * double[][] test = { { 0.25, 0.375 }, { 0.1, 0.5 }, { 0.4, 0.4 }, { 0.4, 0.4 }
       * }; double[] testMean = new double[2]; double[] testStd = new double[2];
       */
      for (int i = 0; i < 89; i++) {
        featureMean = 0;
        sum = 0;
        for (int j = 0; j < 100; j++) {
          sum += newMatrix[j][i];
        }
        featureMean = sum / 100;
        mean[i] = featureMean;

      }
      double featureStd, stdSum;
      for (int i = 0; i < 89; i++) {
        featureStd = 0;
        stdSum = 0;
        for (int j = 0; j < 100; j++) {
          stdSum += Math.pow(newMatrix[j][i] - mean[i], 2);
        }
        featureStd = Math.sqrt(stdSum / 99);
        standardDeviation[i] = featureStd;

      }

      for (int i = 0; i < 100; i++) {
        for (int j = 0; j < 89; j++) {
          if (standardDeviation[j] != 0)
            normalizedMatrix[i][j] = (newMatrix[i][j] - mean[j]) / standardDeviation[j];
          else
            normalizedMatrix[i][j] = 0;

        }
      }
      return normalizedMatrix;
    }
  }

  /**
   * This method is to handle the action on click of Relevance checbox.It enables
   * the checkboxes on the first page
   */
  private class checkBoxHandler implements ActionListener {

    public void actionPerformed(ActionEvent e) {

      displayFirstPage();
    }

  }
}
