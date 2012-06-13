package net.ctdp.tgmutils.io;

import java.util.ArrayList;
import java.util.List;

import net.ctdp.tgmutils.models.TGMFile;
import net.ctdp.tgmutils.models.TGMFile.LookupData;
import net.ctdp.tgmutils.models.TGMFile.Node;
import net.ctdp.tgmutils.models.TGMFile.Node.Ply;
import net.ctdp.tgmutils.models.TGMFile.QuasiStaticAnalysis;
import net.ctdp.tgmutils.models.TGMFile.Realtime;

import org.openmali.vecmath2.Vector3f;

/**
 * TGM Parser is a specialized IniParser for reading TGM-Tire defintions for rFactor2.
 * This parser takes reads the given TGM and returns a {@link TGMFile} object 
 * which stores all information contained in the original tgm-definition.
 * @author danielsenff
 *
 */
public class TGMParser extends AbstractIniParser {

	private TGMFile tgm;
	private int nodeGroupNumber = 0;
	private String oldGroup = null;
	private Node node;
	private Ply ply;

	/**
	 * Create an instance of this parser containing an uninitialized {@link TGMFile}.
	 * After initialization call parse(file) and afterwards retrieve the finished
	 * {@link TGMFile} from the getTGMFile()-method.
	 */
	public TGMParser() {
		this.tgm = new TGMFile();
	}

	/**
	 * Defines how to parse the file.
	 */
	@Override
	protected boolean onSettingParsed(int lineNr, String group, String key,
			String value, String comment) throws ParsingException {

		// only [NODE] so far
//		System.out.println(group);
//		System.out.println(key);
//		System.out.println(value);
		
		if(group.equals("Node")) {
			
			// if we detect new node-group, create new Node-object and add to tgmfile
			if(oldGroup != null && oldGroup == group) {
				// node repeated
			} else {
				this.node = new Node();
				this.tgm.addNode(node);
				nodeGroupNumber++;
			}
			parseNodeGroup(key, value);
		} else if(group.equals("QuasiStaticAnalysis")) {
			parseQuasiStaticAnalysis(key, value);
		} else if(group.equals("Realtime")) {
			parseRealtimeGroup(key, value);
		} else if(group.equals("LookupData")) {
			parseLookupData(key, value);
		}
		
		oldGroup = group;

		return true;
	}


	private void parseLookupData(String key, String value) {
		LookupData lookupData = tgm.getLookupData();
		switch(key) {
		case "Version":
			lookupData.version = value;
			break;
		case "Checksum":
			lookupData.checksum = Integer.valueOf(value);
			break;
		case "Bin":
			lookupData.bins.add(value);
		}
	}


	private void parseQuasiStaticAnalysis(String key, String value) {
		QuasiStaticAnalysis quasiStaticAnalysis = tgm.getQuasiStaticAnalysis();
		switch(key) {
		case "NumLayers":
			quasiStaticAnalysis.numLayers = Integer.valueOf(value);
			break;
		case "NumSections":
			quasiStaticAnalysis.numSections = Integer.valueOf(value);
			break;
		case "RimVolume":
			quasiStaticAnalysis.rimVolume = Double.valueOf(value);
			break;
		case "RealtimeCamberLimit":
			quasiStaticAnalysis.realtimeCamberLimit = Integer.valueOf(value);
			break;
		case "GaugePressure":
			quasiStaticAnalysis.gaugePressures.add(Integer.valueOf(value));
			break;
		case "CarcassTemperature":
			quasiStaticAnalysis.carcassTemperatures.add(Double.valueOf(value));
			break;
		case "RotationSquared":
			quasiStaticAnalysis.rotationSquareds.add(Integer.valueOf(value));
			break;
		case "NumNodes":
			quasiStaticAnalysis.numNodes = Integer.valueOf(value);
			break;
		case "VolumeLoad":
			quasiStaticAnalysis.volumeLoad = Integer.valueOf(value);
			break;
		case "LoadCamber":
			quasiStaticAnalysis.loadCamber = Integer.valueOf(value);
			break;
		case "LoadInclination":
			quasiStaticAnalysis.loadInclination = Integer.valueOf(value);
			break;
		case "LoadDeflection":
			quasiStaticAnalysis.loadDeflection = Integer.valueOf(value);
			break;
		case "TotalMass":
			quasiStaticAnalysis.totalMass = Double.valueOf(value);
			break;
		case "TotalInertiaStandard":
			quasiStaticAnalysis.totalInertiaStandards = parseDoubleList(value);
			break;
		case "RingMass":
			quasiStaticAnalysis.ringMass = Double.valueOf(value);
			break;
		case "RingInertiaStandard":
			quasiStaticAnalysis.ringInertiaStandards = parseDoubleList(value);
			break;
		}
	}


	private void parseNodeGroup(String key, String value) {
		switch(key) {
		case "Geometry":
			node.geometry = parseVector3f(value);
			break;
		case "BulkMaterial":
			node.bulkMaterial.add(parseMaterial(value));
			break;
		case "AnisoCarcassConductivityMult":
			node.anisoCarcassConductivityMult = parseVector3f(value);
			break;
		case "TreadDepth":
			node.treadDepth = Double.valueOf(value);
			break;
		case "TreadMaterial":
			node.treadMaterial.add(parseMaterial(value));
			break;
		case "RingAndRim":
			node.ringAndRim = parseDoubleList(value);
			break;
		case "PlyParams":
			ply = new Ply();
			ply.params = parsePlyParam(value);
			node.plies.add(ply);
			break;
		case "PlyMaterial":
			if(this.ply != null) {
				this.ply.materials.add(parseMaterial(value));
			}
			break;
		}
	}

	
	private void parseRealtimeGroup(String key, String value) {
		Realtime realtime = this.tgm.getRealtime();
		
		switch(key) {
		case "StaticBaseCoefficient":
			realtime.staticBaseCoefficient = Double.valueOf(value);
			break;
		case "SlidingBaseCoefficient":
			realtime.slidingBaseCoefficient = Double.valueOf(value);
			break;
		case "TemporaryBristleSpring":
			realtime.temporaryBristleSpring = parseDoubleList(value);
			break;
		case "TemporaryBristleDamper":
			realtime.temporaryBristleDamper = parseDoubleList(value);
			break;
		case "MarbleEffectOnEffectiveLoad":
			realtime.marbleEffectOnEffectiveLoad = Double.valueOf(value);
			break;
		case "TerrainWeightOnContactTemperature":
			realtime.terrainWeightOnContactTemperature = Double.valueOf(value);
			break;
		case "WLFParameters":
			realtime.wLFParameters = parseDoubleList(value);
			break;
		case "StaticRoughnessEffect":
			realtime.staticRoughnessEffect = Double.valueOf(value);
			break;
		case "GrooveEffects":
			realtime.grooveEffects = parseDoubleList(value);
			break;
		case "DampnessEffects":
			realtime.dampnessEffects = parseDoubleList(value);
			break;
		case "StaticCurve":
			realtime.staticCurve = parseDoubleList(value);
			break;
		case "SlidingAdhesionCurve":
			realtime.slidingAdhesionCurve = parseDoubleList(value);
			break;
		case "SlidingMicroDeformationCurve":
			realtime.slidingMicroDeformationCurve = parseDoubleList(value);
			break;
		case "SlidingMacroDeformationCurve":
			realtime.slidingMacroDeformationCurve = parseDoubleList(value);
			break;
		case "RubberPressureSensitivityPower":
			realtime.rubberPressureSensitivityPower = parseDoubleList(value);
			break;
		case "SizeMultiplier":
			realtime.sizeMultiplier = parseDoubleList(value);
			break;
		case "ThermalDepthAtSurface":
			realtime.thermalDepthAtSurface = Double.valueOf(value);
			break;
		case "ThermalDepthBelowSurface":
			realtime.thermalDepthBelowSurface = Double.valueOf(value);
			break;
		case "BristleLength":
			realtime.bristleLength = Double.valueOf(value);
			break;
		case "InternalGasHeatTransfer":
			realtime.internalGasHeatTransfer = parseDoubleList(value);
			break;
		case "ExternalGasHeatTransfer":
			realtime.externalGasHeatTransfer = parseDoubleList(value);
			break;
		case "GroundContactConductance":
			realtime.groundContactConductance = parseDoubleList(value);
			break;
		case "TireRadiationEmissivity":
			realtime.tireRadiationEmissivity = Double.valueOf(value);
			break;
		case "InternalGasSpecificHeatAtConstantVolume":
			List<Integer> list = parseIntegerList(value);
			realtime.internalGasSpecificHeatAtConstantVolume.put(list.get(0), list.get(1));
			break;
		case "TemporaryAbrasion":
			realtime.temporaryAbrasion = Float.valueOf(value);
			break;
		}
		
	}

	private Node.PlyParam parsePlyParam(String value) {
		String line = value.substring(1, value.length()-1);
		String[] tokens = line.split(",");
		
		Node.PlyParam param = new Node.PlyParam();
		param.plyMaterialAngle = Integer.valueOf(tokens[0]);
		param.plyMaterialThickness = Double.valueOf(tokens[1]);
		param.connectFlat = Integer.valueOf(tokens[2]);
		
		return param;
	}
	
	public Node.Material parseMaterial(String value) {
		String line = value.substring(1, value.length()-1);
		String[] tokens = line.split(",");
		Node.Material material = new Node.Material();
		material.temperature = Double.valueOf(tokens[0]);
		material.density = Double.valueOf(tokens[1]);
		material.youngsModulo = Long.valueOf(tokens[2]); 
		material.poissonsRatio = Double.valueOf(tokens[3]);
		material.dampingFactor  = Double.valueOf(tokens[4]);
		material.specificHeat = Integer.valueOf(tokens[5]);
		material.thermalConductivity = Double.valueOf(tokens[6]);
		return material;
	}

	/**
	 * Returns the finished {@link TGMFile} after parsing.
	 * @return
	 */
	public TGMFile getTGMFile() {
		return this.tgm;
	}

	/**
	 * Parse the given String and return a {@link Vector3f} object.
	 * @param value
	 * @return
	 */
	public static Vector3f parseVector3f(String value) {
		String line = value.substring(1, value.length()-1);
		String[] tokens = line.split(",");

		Vector3f vector = new Vector3f();
		// conversion to float! may be lossy!
		vector.x(Float.valueOf(tokens[0]));
		vector.y(Float.valueOf(tokens[1]));
		vector.z(Float.valueOf(tokens[2]));
		return vector;
	}

	/**
	 * Parse the given String and return a List of double-values.
	 * @param value
	 * @return
	 */
	public static List<Double> parseDoubleList(String value) {
		String line = value.substring(1, value.length()-1);
		String[] tokens = line.split(",");
		List<Double> list = new ArrayList<Double>();
		for (String str : tokens) {
			list.add(Double.valueOf(str));
		}
		return list;
	}
	
	/**
	 * Parse the given String and return a List of integer values.
	 * @param value
	 * @return
	 */
	public static List<Integer> parseIntegerList(String value) {
		String line = value.substring(1, value.length()-1);
		String[] tokens = line.split(",");

		List<Integer> list = new ArrayList<Integer>();
		for (String str : tokens) {
			list.add(Integer.valueOf(str));
		}
		return list;
	}
}
