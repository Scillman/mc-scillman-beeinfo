package eu.scillman.minecraft.beenfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.util.Identifier;

public final class Beenfo
{
    public static final String MOD_ID = "beenfo";

    public static final int NBT_TYPE_INT        = 0x03; // net.minecraft.nbt.NbtInt.getType()
    public static final int NBT_TYPE_LONG       = 0x04; // net.minecraft.nbt.NbtLong.getType()
    public static final int NBT_TYPE_FLOAT      = 0x05; // net.minecraft.nbt.NbtFloat.getType()
    public static final int NBT_TYPE_DOUBLE     = 0x06; // net.minecraft.nbt.NbtDouble.getType()
    public static final int NBT_TYPE_STRING     = 0x08; // net.minecraft.nbt.NbtString.getType()
    public static final int NBT_TYPE_LIST       = 0x09; // net.minecraft.nbt.NbtList.getType()
    public static final int NBT_TYPE_COMPOUND   = 0x0A; // net.minecraft.nbt.NbtCompound.getType()

    public static final Identifier PACKET_ID_LOOKAT = new Identifier(MOD_ID, "client/lookat");
    public static final Identifier PACKET_ID_MENU   = new Identifier(MOD_ID, "server/menu");
    public static final Identifier PACKET_ID_HUD    = new Identifier(MOD_ID, "server/hud");

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
}
