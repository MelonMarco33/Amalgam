package mod.akrivus.amalgam.gem;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.base.Predicate;

import mod.akrivus.amalgam.gem.ai.EntityAIFollowLeaderGem;
import mod.akrivus.amalgam.gem.ai.EntityAIFollowOtherGem;
import mod.akrivus.amalgam.init.AmItems;
import mod.akrivus.amalgam.init.AmSounds;
import mod.akrivus.kagic.entity.EntityGem;
import mod.akrivus.kagic.entity.ai.EntityAICommandGems;
import mod.akrivus.kagic.entity.ai.EntityAIFollowDiamond;
import mod.akrivus.kagic.entity.ai.EntityAIStandGuard;
import mod.akrivus.kagic.entity.ai.EntityAIStay;
import mod.akrivus.kagic.entity.gem.EntityAmethyst;
import mod.akrivus.kagic.entity.gem.GemCuts;
import mod.akrivus.kagic.entity.gem.GemPlacements;
import mod.heimrarnadalr.kagic.util.Colors;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityWatermelonTourmaline extends EntityGem implements IAnimals {
	public static final HashMap<IBlockState, Double> WATERMELON_TOURMALINE_QUARTZ_YIELDS = new HashMap<IBlockState, Double>();
	public static final double WATERMELON_TOURMALINE_QUARTZ_DEFECTIVITY_MULTIPLIER = 2;
	public static final double WATERMELON_TOURMALINE_QUARTZ_DEPTH_THRESHOLD = 72;
	public static final HashMap<Integer, ResourceLocation> WATERMELON_TOURMALINE_QUARTZ_HAIR_STYLES = new HashMap<Integer, ResourceLocation>();
	private static final DataParameter<Integer> LOWER_COLOR = EntityDataManager.<Integer>createKey(EntityCitrine.class, DataSerializers.VARINT);
	
	public static final int LOWER_SKIN_COLOR_BEGIN = 0xFFC9E2; 
	public static final int LOWER_SKIN_COLOR_END = 0xD9A3FF;
	
	public static final int SKIN_COLOR_BEGIN = 0x45E79F; 
	public static final int SKIN_COLOR_END = 0x45AE97;
	
	public static final int HAIR_COLOR_BEGIN = 0xA0FFD6;
	public static final int HAIR_COLOR_END = 0x537066; 
	
	private static final int NUM_HAIRSTYLES = 5;
	
	public EntityWatermelonTourmaline(World worldIn) {
		super(worldIn);
		this.nativeColor = 3;
		
		//Define valid gem cuts and placements
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.BACK_OF_HEAD);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.FOREHEAD);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.LEFT_EYE);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.RIGHT_EYE);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.LEFT_CHEEK);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.RIGHT_CHEEK);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.LEFT_SHOULDER);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.RIGHT_SHOULDER);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.LEFT_HAND);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.RIGHT_HAND);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.BACK);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.CHEST);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.BELLY);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.LEFT_THIGH);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.RIGHT_THIGH);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.LEFT_KNEE);
		this.setCutPlacement(GemCuts.FACETED, GemPlacements.RIGHT_KNEE);
		
		// Apply entity AI.
		this.stayAI = new EntityAIStay(this);
        this.tasks.addTask(1, new EntityAICommandGems(this, 0.6D));
        this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.414D, 32.0F));
        this.tasks.addTask(3, new EntityAIFollowDiamond(this, 1.0D));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(5, new EntityAIStandGuard(this, 0.6D));
        
        // Apply targeting.
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityLiving>(this, EntityLiving.class, 10, true, false, new Predicate<EntityLiving>() {
            public boolean apply(EntityLiving input) {
                return input != null && (IMob.VISIBLE_MOB_SELECTOR.apply(input) || input instanceof EntitySquid);
            }
        }));
        
        // Apply entity attributes.
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
        
        this.droppedGemItem = AmItems.WATERMELON_TOURMALINE_GEM;
		this.droppedCrackedGemItem = AmItems.CRACKED_WATERMELON_TOURMALINE_GEM;
        
        // Register entity data.
        this.dataManager.register(LOWER_COLOR, 0);
	}
	
	/*********************************************************
	 * Methods related to entity loading.                    *
	 *********************************************************/
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.dataManager.set(LOWER_COLOR, this.generateDefectiveColor());
		return super.onInitialSpawn(difficulty, livingdata);
    }
	public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("lowerColors", this.dataManager.get(LOWER_COLOR));
        super.writeEntityToNBT(compound);
    }
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.dataManager.set(LOWER_COLOR, compound.getInteger("lowerColors"));
        super.readEntityFromNBT(compound);
    }

    @Override
    public int generateGemColor() {
    	return 0xA0FFD6;
    }
	
	/*********************************************************
	 * Methods related to sound.                             *
	 *********************************************************/
	protected SoundEvent getHurtSound(DamageSource source) {
		return AmSounds.WATERMELON_TOURMALINE_HURT;
	}
	protected SoundEvent getObeySound() {
		return AmSounds.WATERMELON_TOURMALINE_OBEY;
	}
	protected SoundEvent getDeathSound() {
		return AmSounds.WATERMELON_TOURMALINE_DEATH;
	}
	
	/*********************************************************
	 * Methods related to rendering.                         *
	 *********************************************************/
	@Override
	public int generateSkinColor() {
		ArrayList<Integer> skinColors = new ArrayList<Integer>();
		skinColors.add(EntityWatermelonTourmaline.SKIN_COLOR_BEGIN);
		skinColors.add(EntityWatermelonTourmaline.SKIN_COLOR_END);
		return Colors.arbiLerp(skinColors);
	}
	public int generateDefectiveColor() {
		ArrayList<Integer> skinColors = new ArrayList<Integer>();
		skinColors.add(EntityWatermelonTourmaline.LOWER_SKIN_COLOR_BEGIN);
		skinColors.add(EntityWatermelonTourmaline.LOWER_SKIN_COLOR_END);
		return Colors.arbiLerp(skinColors);
	}
	public int getLowerColor() {
		return this.dataManager.get(LOWER_COLOR);
	}
	@Override
	protected int generateHairStyle() {
		return this.rand.nextInt(EntityWatermelonTourmaline.NUM_HAIRSTYLES);
	}
	@Override
	protected int generateHairColor() {
		ArrayList<Integer> hairColors = new ArrayList<Integer>();
		hairColors.add(EntityWatermelonTourmaline.HAIR_COLOR_BEGIN);
		hairColors.add(EntityWatermelonTourmaline.HAIR_COLOR_END);
		return Colors.arbiLerp(hairColors);
	}

	@Override
	public boolean hasUniformVariant(GemPlacements placement) {
		switch(placement) {
		case BELLY:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public boolean hasCape() {
		return true;
	}
	
	@Override
	public boolean hasHairVariant(GemPlacements placement) {
		switch(placement) {
		case FOREHEAD:
			return true;
		default:
			return false;
		}
	}
}