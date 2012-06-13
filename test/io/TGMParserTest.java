package io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		assertEquals("number of Bins", 1896, lookupData.bins.size());
		String firstbin = "be645b883fef93d5b7b403c23fe43470b7b403d53fe43470e704a27bbc4088ab0d3aa411bc7cd4e8c998faab3fee8696eb9e86f73fe2ecbbeb9e86f33fe2ecbb";
		assertEquals("first bin", firstbin, lookupData.bins.get(0));
		
		assertNotNull("Checksum", lookupData.checksum);
		assertEquals("Checksum", -645463472, lookupData.checksum);
	}

	private static void assertQuasiStaticAnalysis(QuasiStaticAnalysis qsa) {
		assertNotNull("NumLayers", qsa.numLayers);
		assertEquals("NumLayers", 2, qsa.numLayers);
		
		assertNotNull("NumSections", qsa.numSections);
		assertEquals("NumSections", 100, qsa.numSections);
	
		//	RimVolume=0.014
		assertNotNull("RimVolume", qsa.rimVolume);
		assertEquals("RimVolume", 0.014, qsa.rimVolume, 0.0001);
		
		//	RealtimeCamberLimit=45
		assertNotNull("RealtimeCamberLimit", qsa.realtimeCamberLimit);
		assertEquals("RealtimeCamberLimit", 45, qsa.realtimeCamberLimit);
		
		//	GaugePressure=0
		//	GaugePressure=150000
		//	GaugePressure=300000
		assertNotNull("GaugePressure list not null", qsa.gaugePressures);
		assertTrue("GaugePressure", qsa.gaugePressures instanceof List);
		assertEquals("GaugePressure count", 3, qsa.gaugePressures.size());
		assertEquals("GaugePressure values", 0, (int)qsa.gaugePressures.get(0));
		assertEquals("GaugePressure values", 150000, (int)qsa.gaugePressures.get(1));
		assertEquals("GaugePressure values", 300000, (int)qsa.gaugePressures.get(2));
		
		
		//	CarcassTemperature=273.15
		//	CarcassTemperature=373.15
		
		assertNotNull("CarcassTemperature list not null", qsa.carcassTemperatures);
		assertTrue("CarcassTemperature", qsa.carcassTemperatures instanceof List);
		assertEquals("CarcassTemperature count", 2, qsa.carcassTemperatures.size());
		assertEquals("CarcassTemperature values", 273.15, (double)qsa.carcassTemperatures.get(0), 0.0001);
		assertEquals("CarcassTemperature values", 373.15, (double)qsa.carcassTemperatures.get(1), 0.0001);
		
		//	RotationSquared=0
		//	RotationSquared=80000
		assertNotNull("RotationSquared list not null", qsa.rotationSquareds);
		assertTrue("RotationSquared", qsa.rotationSquareds instanceof List);
		assertEquals("RotationSquared count", 2, qsa.rotationSquareds.size());
		assertEquals("RotationSquared values", 0, (int)qsa.rotationSquareds.get(0));
		assertEquals("RotationSquared values", 80000, (int)qsa.rotationSquareds.get(1));
		
		
		//	NumNodes=31
		assertNotNull("NumNodes", qsa.numNodes);
		assertEquals("NumNodes", 31, qsa.numNodes);
		
		//	VolumeLoad=0
		assertNotNull("VolumeLoad", qsa.volumeLoad);
		assertEquals("VolumeLoad", 0, qsa.volumeLoad);
		
		//	LoadCamber=0
		assertNotNull("LoadCamber", qsa.loadCamber);
		assertEquals("LoadCamber", 0, qsa.loadCamber);
		//	LoadInclination=0
		assertNotNull("LoadInclination", qsa.loadCamber);
		assertEquals("LoadInclination", 0, qsa.loadCamber);
		//	LoadDeflection=0
		assertNotNull("LoadDeflection", qsa.loadCamber);
		assertEquals("LoadDeflection", 0, qsa.loadCamber);
		
		//	TotalMass=10.558957270550053
		assertNotNull("TotalMass", qsa.totalMass);
		assertEquals("TotalMass", 10.558957270550053, qsa.totalMass, 0.0001);
		
		//	TotalInertiaStandard=(0.9865952331376638,0.629654560048139,0.6296545600481429)
		assertDoubleList("TotalInertiaStandard", 
				"0.9865952331376638,0.629654560048139,0.6296545600481429", 
				qsa.totalInertiaStandards);
		
		//	RingMass=9.873669634213842
		assertNotNull("RingMass", qsa.ringMass);
		assertEquals("RingMass", 9.873669634213842, qsa.ringMass, 0.0001);
		
		//	RingInertiaStandard=(0.9536807920631629,0.5898999023566385,0.5898999023566396)
		assertDoubleList("RingInertiaStandard", 
				"0.9536807920631629,0.5898999023566385,0.5898999023566396", 
				qsa.ringInertiaStandards);
	}

	private static void assertNode(Node node) {

		// Geometry=(0.17500000000000002,-0.182,0.006)
		assertNotNull("geometry not null", node.geometry);
		assertTrue("geometry should be of type Vector3f", node.geometry instanceof Vector3f);
		assertEquals("geometry X", 0.17500000000000002, node.geometry.x(), 0.0001);
		assertEquals("geometry Y", -0.182, node.geometry.y(), 0.0001);
		assertEquals("geometry T", 0.006, node.geometry.z(), 0.0001);

		assertNotNull("BulkMaterial list not null", node.bulkMaterial);
		assertTrue("BulkMaterial", node.bulkMaterial instanceof List);
		assertEquals("BulkMaterial count", 2, node.bulkMaterial.size());
		assertTrue("BulkMaterial list empty", node.bulkMaterial.size() > 0);
		assertMaterial("first BulkMaterial", "273.15,925,16000000,0.47,-1,1250,3.7", node.bulkMaterial.get(0));
		
		assertNotNull("TreadMaterial list not null", node.treadMaterial);
		assertTrue("TreadMaterial", node.treadMaterial instanceof List);
		assertTrue("TreadMaterial list empty", node.treadMaterial.size() > 0);
		assertEquals("TreadMaterial count", 2, node.treadMaterial.size());
		assertMaterial("first TreadMaterial", "273.15,925,9500000,0.47,-1,2000,0.172", node.treadMaterial.get(0));

		// AnisoCarcassConductivityMult=(1.5,1,1.1)
		assertTrue("anisoCarcassConductivityMult should be of type Vector3f", node.anisoCarcassConductivityMult instanceof Vector3f);
		assertEquals("anisoCarcassConductivityMult X", 1.5, node.anisoCarcassConductivityMult.x(), 0.0001);
		assertEquals("anisoCarcassConductivityMult Y", 1, node.anisoCarcassConductivityMult.y(), 0.0001);
		assertEquals("anisoCarcassConductivityMult Z", 1.1, node.anisoCarcassConductivityMult.z(), 0.0001);

		// TreadDepth=0.003
//		assertTrue("TreadDepth should be of type Double", Double.class, node.treadDepth.getClass);
		assertEquals("TreadDepth", 0.003, node.treadDepth, 0.0001);

		// RingAndRim=(0,1000000000)
		assertDoubleList("RingAndRim", "0,1000000000", node.ringAndRim);
		
		
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
		
		assertEquals("material temperature", 273.15, material.temperature, 0.0001);
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
		
		Map<Integer, Integer> expectedGasSpecificHeat = new HashMap<>();
		expectedGasSpecificHeat.put(250,716);
		expectedGasSpecificHeat.put(300,718);
		expectedGasSpecificHeat.put(350,721);
		expectedGasSpecificHeat.put(400,726);
		expectedGasSpecificHeat.put(450,733);
		expectedGasSpecificHeat.put(500,742);
		assertHashMap("InternalGasSpecificHeatAtConstantVolume", expectedGasSpecificHeat, realtime.internalGasSpecificHeatAtConstantVolume);
		
		assertNotNull("TemporaryAbrasion", realtime.temporaryAbrasion);
		 assertEquals("TemporaryAbrasion", 1.0E-10, realtime.temporaryAbrasion, 0.0001);
	}

	private static void assertHashMap(String message, Map<Integer, Integer> expected, 
			Map<Integer, Integer> actual) {
		int expLength = expected.size();
		int actLength = actual.size();
		assertEquals("hashmap length", expLength, actLength);
		
		for (Integer key : expected.keySet()) {
			Integer value = expected.get(key);
			assertTrue("actual hashmap has key", actual.containsKey(key));
			assertEquals("actual hasmap has key with same value", value, actual.get(key));
			
		}
		
		
	}

	private static void assertDoubleList(String message, String expected,
			List<Double> list) {
		
		assertNotNull(message + " not null", list);
		String[] values = expected.split(",");
		assertEquals(message+" list size", values.length, list.size());
		for (int i = 0; i < values.length; i++) {
			Double value = Double.valueOf(values[i]);
			assertEquals(message+" list value", value, list.get(i));
		}
	}

	/** 
	 * @param filePath the name of the file to open. Not sure if it can accept URLs or just filenames. Path handling could be better, and buffer sizes are hardcoded
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
