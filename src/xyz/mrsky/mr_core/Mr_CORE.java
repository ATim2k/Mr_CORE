package xyz.mrsky.mr_core;

import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.util.Date;

/**
 * Created by WEnGe on 2017/2/18.
 */
public class Mr_CORE extends PluginBase implements Listener{
    @Override
    public void onEnable(){
        plugin = this;
        File playfile = new File(this.getDataFolder()+"\\players");
        playfile.mkdirs();
        this.getLogger().info(TextFormat.YELLOW+"插件正在开启，作者Mr_sky，贴吧ID贱哥啊哈哈");
        this.getServer().getPluginManager().registerEvents(this,this);
        saveResource("config.yml");
        if (!getConfig().exists("ConfigVersion")){
            getLogger().info("请删除旧版config.yml，以令插件更好的运行！");
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Config playerConfig = new Config(this.getDataFolder()+"\\players\\"+event.getPlayer().getName().toLowerCase()+".yml",Config.YAML);
        String 玩家 = String.valueOf(this.getConfig().get("玩家加入"));
        String OP = String.valueOf(this.getConfig().get("OP加入"));
        String 玩家过滤 = 玩家.replace("{player}", event.getPlayer().getName());
        String OP过滤 = OP.replace("{OP}", event.getPlayer().getName());
        if (getConfig().get("是否开启玩家加入信息提示").equals(true)){
            if(event.getPlayer().isOp()) {
                event.setJoinMessage(OP过滤);
            } else {
                event.setJoinMessage(玩家过滤);
            }
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String 玩家 = String.valueOf(this.getConfig().get("玩家退出"));
        String OP = String.valueOf(this.getConfig().get("OP退出"));
        String 玩家过滤 = 玩家.replace("{player}", event.getPlayer().getName());
        String OP过滤 = OP.replace("{OP}", event.getPlayer().getName());
    if (getConfig().get("是否开启玩家退出信息提示").equals(true)){
        if(event.getPlayer().isOp()) {
            event.setQuitMessage(OP过滤);
        } else {
                event.setQuitMessage(玩家过滤);
            }
        }
    }
    @EventHandler
    public void onPlayerDropItem(PlayerDeathEvent event) {
        if (getConfig().get("死亡不掉落物品").equals("true")){
            event.setKeepInventory(true);
            event.getEntity().sendMessage("本服已开启死亡不掉落物品功能，已帮您恢复物品!");
        }else{
            event.setKeepInventory(false);
            event.getEntity().sendMessage("本服未开启死亡不掉落物品功能，无法帮您恢复物品!");
        }
        if (getConfig().get("死亡不掉落经验").equals("true")){
            event.setKeepExperience(true);
            event.getEntity().sendMessage("本服已开启死亡不掉落经验功能，已帮您恢复经验!");
        }else{
            event.setKeepExperience(false);
            event.getEntity().sendMessage("本服未开启死亡不掉落经验功能，无法帮您恢复经验!");
        }
    }
    public String onCurrentTime() {
        Date time = new Date();
        int hours = time.getHours();
        int minutes = time.getMinutes();
        int seconds = time.getSeconds();
        return hours + "点" + minutes + "分" + seconds + "秒";
    }
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (getConfig().get("是否记录指令").equals("true")){
            if (!event.getPlayer().isOp()){
                String wocao = this.onCurrentTime() + " 玩家" + event.getPlayer().getName() + " 使用了指令" + event.getMessage();
                this.getLogger().info(wocao);
            }else{
                String woDiu = "OP" + event.getPlayer().getName() + " 使用了指令" + event.getMessage();
                Config opconfig = new Config(this.getDataFolder()+"\\OP_CommandRecord.yml",Config.YAML);
                opconfig.set(this.onCurrentTime(),woDiu);
                opconfig.save();
                this.getLogger().info("有OP使用了指令，已记录至OP_CommandRecord.yml里!");
            }
        }
    }
    /*接口部分*/
    private static Mr_CORE plugin;
    public static Mr_CORE getPlugin() {
        return plugin;
    }
    public void Lighting(Position pos) {
        BaseFullChunk chunk = pos.getLevel().getChunk((int)pos.getX() >> 4, (int)pos.getZ() >> 4);
        CompoundTag nbt = (new CompoundTag()).putList((new ListTag("Pos")).add(new DoubleTag("", pos.getX())).add(new DoubleTag("", pos.getY())).add(new DoubleTag("", pos.getZ()))).putList((new ListTag("Motion")).add(new DoubleTag("", 0.0D)).add(new DoubleTag("", 0.0D)).add(new DoubleTag("", 0.0D))).putList((new ListTag("Rotation")).add(new FloatTag("", 0.0F)).add(new FloatTag("", 0.0F)));
        EntityLightning lightning = new EntityLightning(chunk, nbt);
        lightning.spawnToAll();
    }
    public Config getPlayerConfig(String playername){
        Config config = new Config(this.getDataFolder()+"\\players\\"+playername.toLowerCase()+".yml",Config.YAML);
        return config;
    }
    public boolean getJoinMessageOnOff(){
        if (getConfig().get("是否开启玩家加入信息提示").equals("true")){
            return true;
        }else{
            return false;
        }
    }
    public boolean getQuitMessageOnOff(){
        if (getConfig().get("是否开启玩家退出信息提示").equals("true")){
            return true;
        }else{
            return false;
        }
    }
    public String getJoinMessage(boolean PlayerOrOP){
        if (PlayerOrOP){
            return getConfig().get("玩家加入").toString();
        }else {
            return getConfig().get("OP加入").toString();
        }
    }
    public String getQuitMessage(boolean PlayerOrOP){
        if (PlayerOrOP){
            return getConfig().get("玩家退出").toString();
        }else {
            return getConfig().get("OP退出").toString();
        }
    }
    public boolean getKeepInventory(){
        if (getConfig().get("死亡不掉落物品").equals("true")){
            return true;
        }else{
            return false;
        }
    }
    public boolean getKeepExperience(){
        if (getConfig().get("死亡不掉落经验").equals("true")){
            return true;
        }else{
            return false;
        }
    }
    public boolean getCommandRecord(){
        if (getConfig().get("是否记录指令").equals("true")){
            return true;
        }else{
            return false;
        }
    }
}
