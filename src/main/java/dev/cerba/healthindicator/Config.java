package dev.cerba.healthindicator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** config/healthindicator.json. 파일이 없거나 읽지 못하면 기본값으로 쓰고 다시 저장한다. */
public final class Config {
    private static final Logger LOG = LoggerFactory.getLogger("healthindicator");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("healthindicator.json");

    /** 체력을 나타내는 모양. */
    public enum Look { NUMBER, NUMBER_MAX, HEARTS, BAR, PERCENT }

    public boolean enabled = true;
    /** NUMBER(15), NUMBER_MAX(15 / 20), HEARTS(하트 그림), BAR(막대), PERCENT(75%). */
    public Look look = Look.HEARTS;
    /** 글자 크기(%). */
    public int scale = 100;
    /** 이 거리(블록) 안의 상대만 표시한다. */
    public int distance = 32;
    public boolean players = true;
    /** 몹·동물. */
    public boolean entities = false;
    /** 서버의 플레이어 모양 NPC(탭 목록에 없는 플레이어). */
    public boolean npcs = false;
    /** 3인칭에서 내 체력. */
    public boolean self = false;
    public boolean hideFull = false;
    /** 흡수 체력(노란 하트)을 함께 표시. */
    public boolean absorption = true;
    /** 체력에 따라 색을 바꾼다(초록→빨강). 끄면 color 를 쓴다. */
    public boolean gradient = true;
    /** RGB(예: 16733525 = 0xFF5555). */
    public int color = 0xFF5555;

    public static Config load() {
        Config c = null;
        if (Files.exists(FILE)) {
            try (Reader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                c = GSON.fromJson(r, Config.class);
            } catch (Exception e) {
                LOG.warn("healthindicator.json 을 읽지 못해 기본값을 씁니다: {}", e.toString());
            }
        }
        if (c == null) c = new Config();
        if (c.look == null) c.look = Look.HEARTS;
        c.scale = Math.max(50, Math.min(200, c.scale));
        c.distance = Math.max(4, Math.min(64, c.distance));
        c.save();
        return c;
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(this, w);
            }
        } catch (Exception e) {
            LOG.warn("healthindicator.json 을 저장하지 못했습니다: {}", e.toString());
        }
    }
}
