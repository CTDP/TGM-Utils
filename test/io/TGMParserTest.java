package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import net.ctdp.tgmutils.io.TGMParser;
import net.ctdp.tgmutils.models.TGMFile;
import net.ctdp.tgmutils.models.TGMFile.LookupData;
import net.ctdp.tgmutils.models.TGMFile.Node;
import net.ctdp.tgmutils.models.TGMFile.Node.Material;
import net.ctdp.tgmutils.models.TGMFile.Node.Ply;
import net.ctdp.tgmutils.models.TGMFile.Node.PlyParam;
import net.ctdp.tgmutils.models.TGMFile.QuasiStaticAnalysis;
import net.ctdp.tgmutils.models.TGMFile.Realtime;

import org.junit.Test;
import org.openmali.vecmath2.Vector3f;

import static org.junit.Assert.*;

public class TGMParserTest {


	@Test
	public void testNodeParsing() {
		try {
			String filePath = "res/node.tgm";

			TGMParser parser = new TGMParser();
			parser.parse(filePath);
			TGMFile tgm = parser.getTGMFile();

			assertNode(tgm.getNodes().get(0));

		} catch (IOException e) {
			assertTrue("exception", false);
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFullParsing() {
		try {
			String filePath = "res/rTrainer_Tires.tgm";

			TGMParser parser = new TGMParser();
			parser.parse(filePath);
			TGMFile tgm = parser.getTGMFile();

			assertTGM(tgm);

		} catch (IOException e) {
			e.printStackTrace();
			assertTrue("exception", false);
		}
	}

	private static void assertTGM(TGMFile tgm) {

		// QuasiStaticAnalysis

		assertTrue("QuasiStaticAnalysis Type", tgm.getQuasiStaticAnalysis() instanceof TGMFile.QuasiStaticAnalysis);
		assertQuasiStaticAnalysis(tgm.getQuasiStaticAnalysis());

		// Nodes

		assertTrue("Realtime is List", tgm.getNodes() instanceof List);
		assertEquals("number of nodes", 31, tgm.getNodes().size());
		assertNode(tgm.getNodes().get(0));

		// Realtime

		assertTrue("Realtime Type", tgm.getRealtime() instanceof TGMFile.Realtime);
		assertRealtime(tgm.getRealtime());

		// LookupData

		assertTrue("LookupData Type", tgm.getLookupData() instanceof TGMFile.LookupData);
		assertLookupData(tgm.getLookupData());
	}

	private static void assertLookupData(LookupData lookupData) {
		assertNotNull("Version", lookupData.version);
		assertTrue("Version type", lookupData.version instanceof String);
		assertEquals("Version", "1.101", lookupData.version);
		
		assertNotNull("Bins", lookupData.bins);
		assertTrue("Bins Type", lookupData.bins instanceof List);
		String firstbin = "be645b883fef93d5b7b403c23fe43470b7b403d53fe43470e704a27bbc4088ab0d3aa411bc7cd4e8c998faab3fee8696eb9e86f73fe2ecbbeb9e86f33fe2ecbb";
		assertEquals("first bin", firstbin, lookupData.bins.get(0));
		
		assertNotNull("Checksum", lookupData.checksum);
		assertEquals("Checksum", -645463472, lookupData.checksum);
	}

	private static void assertQuasiStaticAnalysis(QuasiStaticAnalysis qsa) {
		assertNotNull("NumLayers", qsa.numLayers);
		assertEquals("NumLayers", 2, qsa.numLayers);
		
		// TODO test many more
	}

	private static void assertNode(Node node) {

		// Geometry=(0.17500000000000002,-0.182,0.006)
		assertNotNull("geometry not null", node.geometry);
		assertTrue("geometry should be of type Vector3f", node.geometry instanceof Vector3f);
		assertEquals("geometry X", 0.17500000000000002, node.geometry.x(), 0.0001);
		assertEquals("geometry Y", -0.182, node.geometry.y(), 0.0001);
		assertEquals("geometry T", 0.006, node.geometry.z(), 0.0001);

		// TODO bulkMaterial
		assertNotNull("Material list not null", node.treadMaterial);
		assertTrue("TreadMaterial", node.treadMaterial instanceof List);
		assertTrue("Material list empty", node.treadMaterial.size() > 0);
		assertEquals("TreadMaterial count", 2, node.treadMaterial.size());
		assertMaterial("TreadMaterial", "273.15,925,9500000,0.47,-1,2000,0.172", node.treadMaterial.get(0));

		// AnisoCarcassConductivityMult=(1.5,1,1.1)
		assertTrue("anisoCarcassConductivityMult should be of type Vector3f", node.anisoCarcassConductivityMult instanceof Vector3f);
		assertEquals("anisoCarcassConductivityMult X", 1.5, node.anisoCarcassConductivityMult.x(), 0.0001);
		assertEquals("anisoCarcassConductivityMult X", 1, node.anisoCarcassConductivityMult.y(), 0.0001);
		assertEquals("anisoCarcassConductivityMult X", 1.1, node.anisoCarcassConductivityMult.z(), 0.0001);

		// TreadDepth=0.003
		//		assertTrue("TreadDepth should be of type Double", Double.class, node.treadDepth.getClass);
		assertEquals("TreadDepth", 0.003, node.treadDepth, 0.0001);

		// RingAndRim=(0,1000000000)
		
		
		assertNotNull("Ply List", node.plies);
		assertEquals("Ply List size", 2, node.plies.size());
		
		Ply ply = node.plies.get(0);
		assertPlyParam("PlyParam", "80,0.0004,3", ply.params);
		
		assertTrue("PlyMaterials", ply.materials instanceof List);
		assertEquals("PlyMaterials count", 2, ply.materials.size());
		assertMaterial("PlyMaterial", "273.15,1305,2100000000,0.3,-1,1695,0.25", ply.materials.get(0));
	}


	private static void assertPlyParam(final String message, final String expected,	PlyParam params) {
		
		String[] values = expected.split(",");
		
		assertTrue("PlyParams", params instanceof PlyParam);
		assertEquals("plyMaterialAngle", Integer.valueOf(values[0]), (Integer)params.plyMaterialAngle);
		assertEquals("plyMaterialThickness", Double.valueOf(values[1]), params.plyMaterialThickness, 0.0001);
		assertEquals("connectFlat", Integer.valueOf(values[2]), (Integer)params.connectFlat);
		
	}

	private static void assertMaterial(final String message, final String expected, final Material material) {
		assertTrue(message+" should be of type Material", material instanceof Material);
		
		//		assertEquals("material temperature", 1.5, material.temperature, 0.0001);
	}

	public static void assertRealtime(Realtime realtime) {
		assertNotNull("StaticBaseCoefficient", realtime.staticBaseCoefficient);
		assertEquals("StaticBaseCoefficient", 2.350, realtime.staticBaseCoefficient, 0.0001);
		
		assertNotNull("SlidingBaseCoefficient", realtime.slidingBaseCoefficient);
		assertEquals("SlidingBaseCoefficient", 1.504, realtime.slidingBaseCoefficient, 0.0001);
		
		assertNotNull("SlidingBaseCoefficient", realtime.slidingBaseCoefficient);
		assertEquals("SlidingBaseCoefficient", 1.504, realtime.slidingBaseCoefficient, 0.0001);
		
		assertDoubleList("TemporaryBristleSpring", "24000.0, 12500.0, 31500.0", realtime.temporaryBristleSpring);
		
		assertDoubleList("TemporaryBristleDamper", "0.5, 0.2, 0.5", realtime.temporaryBristleDamper);
		
		assertNotNull("MarbleEffectOnEffectiveLoad", realtime.marbleEffectOnEffectiveLoad);
		assertEquals("MarbleEffectOnEffectiveLoad", -0.125, realtime.marbleEffectOnEffectiveLoad, 0.0001);
		
		assertNotNull("TerrainWeightOnContactTemperature", realtime.terrainWeightOnContactTemperature);
		assertEquals("TerrainWeightOnContactTemperature", 0.1, realtime.terrainWeightOnContactTemperature, 0.0001);
		
		assertDoubleList("WLFParameters", "228.15,50.0,-8.86,51.5", realtime.wLFParameters);
		
		assertNotNull("StaticRoughnessEffect", realtime.staticRoughnessEffect);
		assertEquals("StaticRoughnessEffect", -0.20, realtime.staticRoughnessEffect, 0.0001);
		
		assertDoubleList("GrooveEffects", "0.17,0.17,0.14,0.10", realtime.grooveEffects);
		
		assertDoubleList("DampnessEffects", "-0.125,-0.125,-0.10,-0.075", realtime.dampnessEffects);
		
		assertDoubleList("StaticCurve", "153.0, 0.61, 353.0, 1.176, 653.0, 0.61", realtime.staticCurve);
		
		assertDoubleList("SlidingAdhesionCurve", "-9.20, 0.40, -5.20, 1.70, -1.20, 0.20", realtime.slidingAdhesionCurve);
		
		assertDoubleList("SlidingMicroDeformationCurve", "-5.20, 0.30, -1.20, 1.80, +1.80, 0.30", realtime.slidingMicroDeformationCurve);
		
		assertDoubleList("SlidingMacroDeformationCurve", "-1.20, 0.20, +1.80, 2.00, +4.80, 0.40", realtime.slidingMacroDeformationCurve);
		
		assertDoubleList("RubberPressureSensitivityPower", "-0.075,5000,500000,1", realtime.rubberPressureSensitivityPower);
		
		assertDoubleList("SizeMultiplier", "0.64,0.897", realtime.sizeMultiplier);
	
		assertNotNull("ThermalDepthAtSurface", realtime.thermalDepthAtSurface);
		assertEquals("ThermalDepthAtSurface", 0.0001, realtime.thermalDepthAtSurface, 0.0001);
		
		assertNotNull("ThermalDepthBelowSurface", realtime.thermalDepthBelowSurface);
		assertEquals("ThermalDepthBelowSurface", 0.0004, realtime.thermalDepthBelowSurface, 0.0001);
		
		assertNotNull("BristleLength", realtime.bristleLength);
		assertEquals("BristleLength", 0.20, realtime.bristleLength, 0.0001);
		
		assertDoubleList("InternalGasHeatTransfer", "10.0,5.0,0.6", realtime.internalGasHeatTransfer);
		assertDoubleList("ExternalGasHeatTransfer", "10.0,5.0,0.6", realtime.externalGasHeatTransfer);
		assertDoubleList("GroundContactConductance", "1300.0,0.010", realtime.groundContactConductance);
	
		
		assertNotNull("TireRadiationEmissivity", realtime.tireRadiationEmissivity);
		assertEquals("TireRadiationEmissivity", 0.90, realtime.tireRadiationEmissivity, 0.0001);
		
		/*assertDoubleList("InternalGasSpecificHeatAtConstantVolume", "1300.0,0.010", realtime.internalGasSpecificHeatAtConstantVolume);
		
	case "InternalGasSpecificHeatAtConstantVolume":
		List<Integer> list = parseIntegerList(value);
		realtime.internalGasSpecificHeatAtConstantVolume.put(list.get(0), list.get(1));
		break;*/
		
		assertNotNull("TemporaryAbrasion", realtime.temporaryAbrasion);
//		TODO assertEquals("TemporaryAbrasion", 10, realtime.temporaryAbrasion, 0.0001);
	}


	private static void assertDoubleList(String message, String expected,
			List<Double> list) {
		assertNotNull(message, list);
		String[] values = expected.split(",");
		assertEquals(message+" list size", values.length, list.size());
		for (int i = 0; i < values.length; i++) {
			Double value = Double.valueOf(values[i]);
			assertEquals(message+" list value", value, list.get(i));
		}
		
	}

	/** @param filePath the name of the file to open. Not sure if it can accept URLs or just filenames. Path handling could be better, and buffer sizes are hardcoded
	 */ 
	private static String readFileAsString(String filePath)
			throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(
				new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
}
