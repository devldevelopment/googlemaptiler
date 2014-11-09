package com.devl.development.googlemaptiler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Copyright 2014 Dev Lakhani
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class TilesGenerator {
  static int TILE_SIZE = 256;
  static String TILE_FILENAME = "tile_%d_%d_%d.png";
  static String OUTPUT_DIR = "./output";

  public static void main(String[] args) {
    int level = Integer.parseInt(args[0]);

    if (args.length != 2 || level < 1 || level > 15 || !new File(args[1]).exists()) {
      System.err.println("usage: \"TilesGenerator [1-15] [filename]\"");
      return;
    }

    try {
      int mapWidth = GetMapWidth(level);

      if (!new File(OUTPUT_DIR).exists()) {
        new File(OUTPUT_DIR).mkdir();
      }

      BufferedImage original = null;
      try {
        original = ImageIO.read(new File(args[1]));

        SplitTilesRecursive(original, level);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static void SplitTilesRecursive(BufferedImage original, int level) {
    int mapWidth = GetMapWidth(level);
    int tilesOnSide = mapWidth / TILE_SIZE;
    BufferedImage resized = ResizeImage(original, mapWidth * 2, mapWidth);

    for (int x = 0; x < tilesOnSide * 2; x++)
      for (int y = 0; y < tilesOnSide; y++)
        CropAndSaveTile(resized, x, y, level);

    if (level > 0)
      SplitTilesRecursive(original, level - 1);
  }

  private static int GetMapWidth(int level) {
    return TILE_SIZE * (int) Math.pow(2, level);
  }

  private static void CropAndSaveTile(BufferedImage image, int x, int y, int level) {
    Rectangle cropArea = new Rectangle(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

    BufferedImage clone = deepCopy(image);
    BufferedImage cropped = cropImage(clone, cropArea);
    String filename = String.format(TILE_FILENAME, level, x, y);
    try {
      ImageIO.write(cropped, "png", new File(OUTPUT_DIR + "/" + filename));
      System.err.println("Done " + filename);
      cropped = null;
      clone = null;
    } catch (IOException e) {

      e.printStackTrace();
    }
  }

  private static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
    BufferedImage dest = src.getSubimage(rect.x, rect.y, rect.width, rect.height);
    return dest;
  }

  private static BufferedImage ResizeImage(BufferedImage toResize, int width, int height) {
    BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = resizedImage.createGraphics();
    //improve resize quality
    g.setComposite(AlphaComposite.Src);
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.drawImage(toResize, 0, 0, width, height, null);
    g.dispose();
    return resizedImage;

  }

  private static BufferedImage deepCopy(BufferedImage bi) {
    BufferedImage copyOfImage =
        new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = copyOfImage.createGraphics();
    g.drawImage(bi, 0, 0, null);
    return copyOfImage;
  }

}
