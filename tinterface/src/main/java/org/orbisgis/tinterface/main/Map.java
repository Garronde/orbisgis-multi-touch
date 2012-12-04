package org.orbisgis.tinterface.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.util.math.Vector3D;
import org.orbisgis.core.context.main.MainContext;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.OwsMapContext;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.progress.NullProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

import processing.core.PImage;

/**
 * Constructor of the class Map
 * 
 * @author patrick
 * 
 */
public class Map extends MTRectangle {
	
	public final MainFrame frame;
	public final MapContext mapContext;
	public MTApplication mtApplication;
	
	public Map(MTApplication mtApplication, MainScene mainScene)
			throws Exception {
		super(mtApplication, mtApplication.width, mtApplication.height);
		this.mtApplication=mtApplication;
		this.unregisterAllInputProcessors();
		this.removeAllGestureEventListeners();

		MainContext.initConsoleLogger(true);
		CoreWorkspace workspace = new CoreWorkspace();
		File workspaceFolder = new File(System.getProperty("user.home"),
				"OrbisGIS_MT" + File.separator);
		workspace.setWorkspaceFolder(workspaceFolder.getAbsolutePath());
		MainContext mainContext = new MainContext(true, workspace, true);
		frame = new MainFrame();
		mapContext = getSampleMapContext();
		frame.init(mapContext);
        mapContext.draw(frame.mapTransform, new NullProgressMonitor());


		BufferedImage im = frame.mapTransform.getImage();
		PImage image = new PImage(im);

		this.setTexture(image);
		mainScene.getCanvas().addChild(this);
	}

	/**
	 * Method used to move the map from the parameter in input
	 * 
	 * @param x
	 *            the number of pixel the map need to be moved (in x)
	 * @param y
	 *            the number of pixel the map need to be moved (in y)
	 */
	public void move(float x, float y) {
		Envelope extent = frame.mapTransform.getExtent();
		double dx = x*extent.getWidth()/mtApplication.width;
		double dy = y*extent.getHeight()/mtApplication.height;
		frame.mapTransform.setExtent(
				new Envelope(extent.getMinX() - dx, extent.getMaxX() - dx,
					extent.getMinY() + dy, extent.getMaxY() + dy));
		frame.mapTransform.setImage(new BufferedImage(frame.mapTransform.getWidth(), frame.mapTransform.getHeight(), BufferedImage.TYPE_INT_ARGB));
        mapContext.draw(frame.mapTransform, new NullProgressMonitor());

		BufferedImage im = frame.mapTransform.getImage();
		PImage image = new PImage(im);
		this.setTexture(image);

	}

	private static MapContext getSampleMapContext()
			throws IllegalStateException, LayerException {
		MapContext mapContext = new OwsMapContext();
		InputStream fileContent = Map.class.getResourceAsStream("Iris.ows");
		mapContext.read(fileContent);
		mapContext.open(null);
		return mapContext;
	}

	/**
	 * This function get the informations corresponding the the position of the input vector
	 * @param vector the vector corresponding the the position
	 * @return the information about this position (String)
	 */
	public String getInfos(Vector3D vector) {
		// TODO Auto-generated method stub
		return "No information available";
	}

	public PImage getThumbnail() {
		BufferedImage im = frame.mapTransform.getImage();
		PImage image = new PImage(im);
		return image;
	}

	public void scale(float scaleFactorX, float scaleFactorY) {
		Envelope extent = frame.mapTransform.getExtent();
		frame.mapTransform.setExtent(
				new Envelope(extent.getMinX()+(scaleFactorX-1)*extent.getWidth(), extent.getMaxX()-(scaleFactorX-1)*extent.getWidth(),
					extent.getMinY()+(scaleFactorY-1)*extent.getHeight(), extent.getMaxY()-(scaleFactorY-1)*extent.getHeight()));
		frame.mapTransform.setImage(new BufferedImage(frame.mapTransform.getWidth(), frame.mapTransform.getHeight(), BufferedImage.TYPE_INT_ARGB));
        mapContext.draw(frame.mapTransform, new NullProgressMonitor());

		BufferedImage im = frame.mapTransform.getImage();
		PImage image = new PImage(im);
		this.setTexture(image);		
	}
}
