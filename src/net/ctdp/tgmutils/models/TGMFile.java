package net.ctdp.tgmutils.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmali.vecmath2.Vector3f;

/**
 * 
 * 
 * Comments for value fields taken from ISI's TGM documentation.
 * @author danielsenff
 *
 */
public class TGMFile {
	private QuasiStaticAnalysis quasiStaticAnalysis;
	private List<Node> nodes;
	private Realtime realtime;
	private LookupData lookupData;

	/**
	 * Initialize an empty {@link TGMFile}.
	 */
	public TGMFile() {
		this.quasiStaticAnalysis = new QuasiStaticAnalysis();
		this.nodes = new ArrayList<Node>();
		this.realtime = new Realtime();
		this.lookupData = new LookupData();
	}

	/**
	 * This section defines which tests are to be run
	 * @author danielsenff
	 *
	 */
	public class QuasiStaticAnalysis {
		/**
		 * currently fixed
		 */
		public int 		numLayers;
		/**
		 * number of sections around the circumference.
		 */
		public int 		numSections;
		/**
		 * Rim volume, used for total tyre air volume calculations.
		 */
		public double 	rimVolume;
		/**
		 * Helps to efficiently translate the geometry into the realtime brush model, 
		 * Basically so we don't waste bristles on the sidewall. We believe that 45 is a good compromise, but it would need to be bigger for motorcycle racing, also if anyone wants to have a go at creating a 2 wheel stunt driving mod ;) ;).
		 */
		public int 		realtimeCamberLimit;
		/**
		 * GaugePressure=0 // Min should probably always be 0, to simulate a flat tyre (not currently implemented so you might even consider skipping this test ATM to speed up things, but you will need to rerun the testing once this is functioning!).
		 * GaugePressure=150000 // I like to use the typical cold pressure used for the tyre as this value, you can add more entries for better accuracy, but the tables take longer to generate.
		 * //GaugePressure=200000 // If you decide to add more detail, a test running near the optimum real life pressure would be a good idea too, we recommend up to 5 of these tests for GaugePressure
		 * GaugePressure=300000 // Max should be something slightly higher than you will ever be likely to reach, even while doing burnouts
		 */
		public List<Integer> gaugePressures = new ArrayList<Integer>();
		/**
		 * CarcassTemperature=273.15 // Should be the minimum temperature this tyre is ever likely to be subjected to (at the carcass depth!).
		 * CarcassTemperature=373.15 // Something near the optimum here might be a good idea..
		 * //CarcassTemperature=423.15 // Should be the max temperature this tyre is likely to ever be subjected to.
		 */
		public List<Double> carcassTemperatures = new ArrayList<Double>();
		/**
		 * RotationSquared=0 // Minimum speed should probably always be 0 
		 * //RotationSquared=15000 // Optional for better accuracy at varying speeds 
		 * //RotationSquared=40000 // Optional for better accuracy at varying speeds	
		 * RotationSquared=80000 // Maximum lookup table speed, should approximate top speed of the vehicle tyre is intended for in (radians/sec)^2 (consider going a bit higher if this tyre might be used for something faster in the future)
		 */
		public List<Integer> rotationSquareds = new ArrayList<Integer>();
		/**
		 * number of total nodes, not recommended to increase (at least during 2012) as performance cost is substantial, this value is generated from the number of [nodes] listed below
		 */
		public int 		numNodes;
		public int 		volumeLoad;
		public int 		loadCamber;
		public int 		loadInclination;
		public int 		loadDeflection;
		/**
		 * total tyre masses and inertia's as calculated by ttool
		 */
		public double 	totalMass;
		public List<Double> totalInertiaStandards = new ArrayList<>(); //=(0.9865952331376638,0.629654560048139,0.6296545600481429)
		public double	ringMass;
		public List<Double> ringInertiaStandards = new ArrayList<>(); //=(0.9536807920631629,0.5898999023566385,0.5898999023566396)
	}

	public static class Node {
		/**
		 * Location of the node X (lateral displacement from centreline), Y (vertical displacement from centre), T (Thickness of the node). This is the outer surface of the tyre, the depth defines the inner point.
		 */
		public Vector3f		geometry; //=(-0.17500000000000002,-0.182,0.006) (X, Y, T (Thickness))
		/**
		 * Fills out the tyre with this type of material for where there are no other materials
		 */
		public List<Material>	bulkMaterial = new ArrayList<Material>(); //=(273.15,925,16000000,0.47,-1,1250,3.7)
			// bulkMaterial; //=(373.15,903,12000000,0.47,-1,1290,3.52)
		public Vector3f		anisoCarcassConductivityMult; //=(1.5,1,1.1)
		public double		treadDepth; //=0.003
		/**
		 * Surface contact properties are defined by this material
		 */
		public List<Material> treadMaterial = new ArrayList<Material>();
		//treadMaterial; //=(373.15,903,7300000,0.47,-1,2200,0.165)
			//treadMaterial; //=(273.15,925,9500000,0.47,-1,2000,0.172)
		/**
		 * 1st value whether mass contained within the node is calculated as 
		 * part of the 6 DOF Rigid Ring (1.0, which is defined as approximately 
		 * half way up the sidewall and inclusive of the tread area) or part 
		 * of the rim (0.0). Rim is the spring rate of the nodes' connection 
		 * to the rim. Essentially this should represent the spring rate of the 
		 * bead itself. There should be only 2 nodes connected to the rim, 
		 * the first and final nodes.
		 */
		public List<Double>	ringAndRim; //=(0,1000000000) // das ringrim ding ist eigentlich immer 0,0 ausser bei den nodes, die auf der felge aufliegen, also dem ersten und letzten
		/**
		 * Material layers
		 */
		public List<Ply> plies = new ArrayList<Ply>();; //=(80,0.0004,3)
		/**
		 * PlyMaterial = Carcass construction properties are defined by this material
		 */

		public static class Ply {
			public PlyParam params; // winkel in relation zur lauffläche, dicke der fasern, mit welchen nodes sie vernüpft sind
			public List<Material> materials = new ArrayList<>();
		}
		
		public static class Material {
			/**
			 * Temperature in Kelvin
			 */
			public double temperature;
			/**
			 * Density
			 */
			public double density;
			/**
			 * Young's Modulus
			 */
			public Long youngsModulo; 
			public double poissonsRatio;
			/**
			 * Damping Factor
			 * not implemented in rf2, leave -1
			 */
			public double dampingFactor = -1;
			public int specificHeat;
			public double thermalConductivity;
			
			
		}
		public static class PlyParam {
			/**
			 * is the ply material angle relative to circumferential tread 
			 * center line (i.e. 0 degrees is perfectly forward / rearward, 
			 * rarely seen in normal tyres but more common for motorcycle tyres, 
			 * 90 degrees is a radially wound ply
			 */
			public int plyMaterialAngle;
			/**
			 *  represents the ply material thickness.
			 */
			public double plyMaterialThickness;
			/**
			 * 'connect flag' ie. connects to previous node = 1, 
			 * connects to next node = 2, connects to both previous 
			 * and next node = 3, this flag can help blend transitions
			 */
			public int connectFlat;
		}
	}

	/**
	 * These parameters are only in effect running the real time model, 
	 * they do not effect the QSA model (Quasi-Static Analysis) what-so-ever. 
	 * Changes in these lines can be tested immediately in- game by simple 
	 * clicking 'race' and driving on track, while using the developer exe.
	 * @author danielsenff
	 *
	 */
	public class Realtime {
		public double 	staticBaseCoefficient; //2.350
		public double 	slidingBaseCoefficient; //1.504
		/**
		 * Lat/Vert/Long
		 */
		public List<Double> temporaryBristleSpring; //(24000.0, 12500.0, 31500.0)
		public List<Double> temporaryBristleDamper; //(0.5, 0.2, 0.5)
		/**
		 * load available for grip is reduced by 12.5% driving on marbles
		 */
		public double 	marbleEffectOnEffectiveLoad; //-0.125  
		/**
		 * the temperature used for WLF is influenced by the 
		 * track temperature (in this case, 90% tire surface, 10% terrain surface)
		 */
		public double 	terrainWeightOnContactTemperature; //0.1
		/**
		 * Glass temperature, other values are pretty much the same for all 
		 * rubbers, with the exception of butyl. 
		 * Most likely, you won't touch these last three values.
		 */
		public List<Double> wLFParameters; //(228.15,50.0,-8.86,51.5)
		/**
		 * terrain roughness (discussed later) should decrease static friction
		 */
		public double 	staticRoughnessEffect; //-0.20 
		/**
		 * maximum groove increases grip by 10-20% here for: static friction, 
		 * sliding adhesion, sliding micro-deformation, sliding macro-deformation
		 */
		public List<Double>	grooveEffects; //=(0.17,0.17,0.14,0.10)
		/**
		 * fully damp track (at threshold of standing water or more) decreases 
		 * grip by 15-25% here for same things as GrooveEffects
		 */
		public List<Double>	dampnessEffects; //=(-0.125,-0.125,-0.10,-0.075)
		/**
		 * at -100C there's 60% of maximum static grip, at 100C it's maximum, 
		 * at 400C it's back down to 60% of max static grip
		 */
		public List<Double>	staticCurve; //=(153.0, 0.61, 353.0, 1.176, 653.0, 0.61)
		public List<Double>	slidingAdhesionCurve; //=(-9.20, 0.40, -5.20, 1.70, -1.20, 0.20 );
		public List<Double>	slidingMicroDeformationCurve;//=(-5.20, 0.30, -1.20, 1.80, +1.80, 0.30 )
		public List<Double>	slidingMacroDeformationCurve; //=(-1.20, 0.20, +1.80, 2.00, +4.80, 0.40 )
		/**
		 * power,offset,nominal_max,normalize
		 */
		public List<Double>	rubberPressureSensitivityPower; //=(-0.075,5000,500000,1)
		/**
		 * if necessary, an adjustment to the geometric width and radius; default is (1.0,1.0)
		 */
		public List<Double>	sizeMultiplier; //=(0.64,0.897)
		/**
		 * the depth of the temperature sample layer used for contact properties 
		 * (i.e. grip and wear); if provisional second layer is disabled, 
		 * tread will never be allowed to get thinner than this value
		 */
		public double 	thermalDepthAtSurface; //=0.0001
		/**
		 * (if provisional code enabled) the depth of the second layer; 
		 * value should be >= surface layer but not too big; 
		 * tread will never be allowed to get thinner than these two layers
		 */
		public double 	thermalDepthBelowSurface; //=0.0004
		/**
		 * tuned to aid collision detection, no other physical effects
		 */
		public double	bristleLength; //=0.20
		/**
		 * (base, mult, power) - heat transfer coefficients to internal 
		 * gas cavity = base+(mult*(vel^power)), where vel is linear velocity of tire
		 */
		public List<Double>	internalGasHeatTransfer; //=(10.0,5.0,0.6)
		/**
		 * (base, mult, power) - heat transfer coefficients to external air = base+(mult*(vel^power)), where vel is linear velocity of tire
		 */
		public List<Double>	externalGasHeatTransfer; //=(10.0,5.0,0.6)
		/**
		 * (base, mult) - thermal contact conductance coefficient to ground = base+(mult*pressure), where pressure is contact pressure
		 */
		public List<Double>	groundContactConductance; //=(1300.0,0.010)
		/**
		 * thermal radiation emissivity for external tire surface
		 */
		public double	tireRadiationEmissivity; //=0.90 

		/**
		 * (250,716) - (temperature (K), specific heat at constant volume (J/(kg*K)))
		 * (300,718) - 719 J/(kg*K) is an approximation for dry air, but value
		 * (350,721) - changes slightly depending on temperature
		 * 
		 * you may have other issues if the internal gas reaches 500 degrees Kelvin
		 */
		public Map<Integer, Integer> internalGasSpecificHeatAtConstantVolume = new HashMap<>();
		
		public float	temporaryAbrasion; //=1e-10 TODO fix parsing
	}

	/**
	 * 
	 * @author danielsenff
	 *
	 */
	public class LookupData {
		public String version;
		public List<String> bins = new ArrayList<>();
		public int checksum;
	}

	/**
	 * Return all nodes contained in this TGM defintion.
	 * @return
	 */
	public List<Node> getNodes() {
		return this.nodes;
	}

	/**
	 * Add a new node to this TGM definition.
	 * @param node
	 */
	public void addNode(Node node) {
		this.nodes.add(node);
	}

	/**
	 * Return QuasiStaticAnalysis
	 * @return
	 */
	public QuasiStaticAnalysis getQuasiStaticAnalysis() {
		return this.quasiStaticAnalysis;
	}

	/**
	 * Return Realtime
	 * @return
	 */
	public Realtime getRealtime() {
		return this.realtime;
	}

	/**
	 * Return LookupData
	 * @return
	 */
	public LookupData getLookupData() {
		return this.lookupData;
	}
}
