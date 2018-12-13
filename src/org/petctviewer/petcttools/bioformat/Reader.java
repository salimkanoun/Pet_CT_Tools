package org.petctviewer.petcttools.bioformat;



/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import java.io.File;
import java.io.IOException;

import ij.ImagePlus;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import loci.plugins.util.ImageProcessorReader;

/**
 * Reads all files in given directory in parallel,
 * using a separate thread for each.
 */
public class Reader implements Runnable {
  private String id;

  public Reader(String id) {
    this.id = id;
    ImageProcessorReader ipReader= new ImageProcessorReader();
    //ipReader.
  }

  public void run() {
    try {
      ImageReader r = new ImageReader();
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      IMetadata meta = service.createOMEXMLMetadata();
      r.setMetadataStore(meta);
      r.setId(id);
      
      System.out.println(Thread.currentThread().getName() +
        ": id=" + id +
        ", sizeX=" + r.getSizeX() +
        ", sizeY=" + r.getSizeY() +
        ", sizeZ=" + r.getSizeZ() +
        ", sizeT=" + r.getSizeT() +
        ", sizeC=" + r.getSizeC() +
        ", imageName=" + meta.getImageName(0));
      r.close();
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
  }

  public static void main(String[] args) {
    String dir = args[0];
    File[] list = new File(dir).listFiles();
    for (int i=0; i<list.length; i++) {
    	Reader pr = new Reader(list[i].getAbsolutePath());
      new Thread(pr).start();
    }
  }
  
}