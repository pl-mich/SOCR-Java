/****************************************************
Statistics Online Computational Resource (SOCR)
http://www.StatisticsResource.org
 
All SOCR programs, materials, tools and resources are developed by and freely disseminated to the entire community.
Users may revise, extend, redistribute, modify under the terms of the Lesser GNU General Public License
as published by the Open Source Initiative http://opensource.org/licenses/. All efforts should be made to develop and distribute
factually correct, useful, portable and extensible resource all available in all digital formats for free over the Internet.
 
SOCR resources are distributed in the hope that they will be useful, but without
any warranty; without any explicit, implicit or implied warranty for merchantability or
fitness for a particular purpose. See the GNU Lesser General Public License for
more details see http://opensource.org/licenses/lgpl-license.php.
 
http://www.SOCR.ucla.edu
http://wiki.stat.ucla.edu/socr
 It s Online, Therefore, It Exists! 
****************************************************/
/**
 * DistributionGraphPanel.java 0.1 06/05/03  Ivo D. Dinov, Ph.D., Jianming, Rahul Gidwani.
 * He This code was originially written by Ivo, Jianming modified it, later modified by Rahul Gidwani.
 */

package edu.ucla.stat.SOCR.core;
import java.awt.*;

import java.awt.event.*;
import java.text.*;

import javax.swing.*;

import edu.ucla.stat.SOCR.distributions.*;

public class DistributionGraphPanel extends GraphPanels implements MouseListener,
MouseMotionListener 
{
	/**
	 * @param container
	 * Calls the constructor for the Super Class.
	 */

	public DistributionGraphPanel(SOCRDistributionFunctors container) {
		super(container);
	}

	/**
	 * @uml.property name="PANELFILE"
	 * PANELFILE is the name of the textfile stored on the system which 
	 * populates the drop down menus.
	 */
	public final static String PANELFILE = "implementedDistributions.txt";

	/**
	 * @uml.property name="dist"
	 * Sets the distribution for viewing PDF and CDF's
	 */
	public void setDistribution(Distribution d) {
		if (d != null) {
			dist = d;
			domain = dist.getDomain();
			type = dist.getType();
			left = domain.getLowerBound();
			right = domain.getUpperBound();

			if (dist.getMaxDensity() < 1) {
				super.setScale(left, right, 0, dist.getMaxDensity() / 0.7);
			} else {
				setScale(left, right, 0, dist.getMaxDensity());
			}
		}
		repaint();
	}

	/** This method returns the filename for which distributions belong to this panel. */
	public String getPanelFile()
	{   return PANELFILE;
	}

	/** 
	 * Method to graph the PDF/CDF for a particular function to the screen.
	 */
	public void paintComponent(Graphics g) 
	{

		super.paintComponent(g);

		(javax.swing.SwingUtilities.getRoot(this)).validate();
		container.updateStatus();

		if (dist != null) {
			double x, a, b, delta, width, values;

			width = domain.getWidth();
			values = domain.getSize();

			//Draw axes
			g.setColor(Color.black);
			drawAxis(g, 0, yMax, 0.1 * yMax, xMin, VERTICAL);
			drawAxis(g, domain, 0, HORIZONTAL, type);
			g.drawLine(leftMargin, topMargin, getWidth() - rightMargin, topMargin);
			g.drawLine(getWidth() - rightMargin, topMargin, getWidth()
					- rightMargin, getHeight() - bottomMargin);

			//Draw distribution getDensity
			g.setColor(Color.red);
			if (type == DISCRETE) {
				for (int i = 0; i < values; i++) {
					x = domain.getValue(i);
					drawBox(g, x - width / 2, 0, x + width / 2, dist.getDensity(x));
				}
				a = left;
				b = right;
				delta = (b - a) / (getWidth() - leftMargin - rightMargin);
				for (x = a; x < b; x = x + delta) {
					drawLine(g, x, dist.getDensity(x), x, 0);
				}
			} else {
				a = domain.getLowerValue();
				b = domain.getUpperValue();
				delta = (b - a) / (getWidth() - leftMargin - rightMargin);
				for (x = a; x < b; x = x + delta) {
					drawLine(g, x, dist.getDensity(x), x + delta, dist.getDensity(x
							+ delta));
				}
				a = left;
				b = right;
				for (x = a; x < b; x = x + delta) {
					drawLine(g, x, dist.getDensity(x), x, 0);
				}
			}

			//Draw Cursor Position
			if (xPosition > leftMargin && xPosition < getWidth() - rightMargin
					&& yPosition > topMargin
					&& yPosition < getHeight() - bottomMargin) {
				g.setColor(Color.black);

				int y1 = yGraph(dist.getDensity(xScale(xPosition)));
				g.drawLine(xPosition, y1 - 6, xPosition, y1 + 6);
				g.drawLine(xPosition - 6, y1, xPosition + 6, y1);

				String s1 = "";
				if (type == DISCRETE) {
					s1 = "( " + (int) (xScale(xPosition) + 0.5 * domain.getWidth())
					/ domain.getWidth() + ", "
					+ format(dist.getDensity(xScale(xPosition))) + " )";
				} else {
					s1 = "( " + format(xScale(xPosition)) + ", "
					+ format(dist.getDensity(xScale(xPosition))) + " )";
				}
				g.drawString(s1, xPosition + 5, y1 - 5);
			}
		}
	}
}
