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

package edu.ucla.stat.SOCR.core;


/**
 * This class stores a static final URL that stores the codebase.
 * This should be set by any JApplet class so that dependant classes 
 * will have access to this URL. 
 */
import java.net.*;
public class SOCRCodeBase {
private static URL codeBase;

	public SOCRCodeBase() {
	//codeBase = url;
	}

	public static URL getCodeBase() {
	return codeBase;
	}	
	public static void setCodeBase(URL url) {
	codeBase = url;
	}
	

}
  
