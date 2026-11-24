package dev.cerba.healthindicator;

import dev.cerba.healthindicator.Config.Look;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 머리 위 체력. 서버가 보내 주는 체력 값만 쓴다.
 * 안 보이는 상대의 위치를 알려 주지 않도록, 다음 상대는 표시하지 않는다.
 * - 몸(머리·가슴·발)이 블록에 가려 카메라에서 보이지 않는 상대(높이·좌우를 옮겨도 체력만 벽 위로 보이지 않게)
 * - 투명한 상대(투명화 물약)
 * - 서버가 팀 설정으로 이름표를 숨긴 플레이어, 웅크린 플레이어
 * 글자와 하트는 깊이 검사를 하는 층에 그려서 벽 너머로 비치지 않는다.
 */
public final class HealthRenderer {
    private static final String HEART = "❤";
    /** 머리와 체력 표시 사이, 이름표와 체력 표시 사이의 틈(블록). */
    private static final float HEAD_GAP = 0.15f, NAME_GAP = 0.03f;
    /** 바닐라 이름표: 바닥은 머리에서 0.275 위, 글자 한 줄 높이 0.225, 서버 점수 줄이 있으면 이름이 0.25875 올라간다. */
    private static final float LABEL_BASE = 0.275f, LINE = 0.225f, LABEL_STEP = 0.25875f;

    private static final Identifier ICONS = new Identifier("textures/gui/icons.png");
    private static final int U_EMPTY = 16, U_FULL = 52, U_HALF = 61, U_ABS_FULL = 160, U_ABS_HALF = 169;
    private static final int HEARTS_PER_ROW = 10, MAX_HEART_CELLS = 20;

    private static final Map<Integer, Boolean> SEEN = new HashMap<>();
    private static long seenTick = Long.MIN_VALUE;

    private HealthRenderer() {}

    // ── 그리기 ──────────────────────────────────────────────

    /** LivingEntityRenderer 가 생물을 다 그린 뒤 부른다. */
    public static void render(LivingEntity e, MatrixStack matrices, VertexConsumerProvider consumers, EntityRenderDispatcher dispatcher) {
        Config c = HealthIndicator.config;
        if (c == null || !visible(c, e, dispatcher)) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        int color = c.gradient ? gradient(ratio(e)) : c.color;
        float s = 0.025f * c.scale / 100f;
        // 높이·좌우 설정은 1블록 = 100 이다. 글자는 y 에서 아래로 그려진다.
        double y = e.getHeight() + bottom(e, c) + pixels(e, c) * s;
        matrices.push();
        matrices.translate(0, y, 0);
        matrices.multiply(dispatcher.getRotation());
        // 카메라 쪽 축은 화면 오른쪽이 음수다.
        matrices.translate(-c.side / 100.0, 0, 0);
        matrices.scale(-s, -s, s);
        if (icons(e, c)) {
            drawHearts(e, c, matrices, consumers);
        } else {
            TextRenderer tr = mc.textRenderer;
            Text t = text(e, c.look, c.absorption, color);
            float x = -tr.getWidth(t) / 2f;
            int bg = (int) (mc.options.getTextBackgroundOpacity(0.25f) * 255f) << 24;
            // NORMAL: 벽에 가려지면 보이지 않는다(투시가 되지 않게).
            tr.draw(t, x, 0, 0xFFFFFFFF, false, matrices.peek().getPositionMatrix(), consumers, TextRenderer.TextLayerType.NORMAL, bg,
                    LightmapTextureManager.MAX_LIGHT_COORDINATE);
        }
        matrices.pop();
    }

    /** 하트 그림은 사각형으로 그린다. 글자와 같은 층을 써서 벽에 가려지는 것도 같다. */
    private static void drawHearts(LivingEntity e, Config c, MatrixStack matrices, VertexConsumerProvider consumers) {
        VertexConsumer vc = consumers.getBuffer(RenderLayer.getText(ICONS));
        Matrix4f m = matrices.peek().getPositionMatrix();
        int light = LightmapTextureManager.MAX_LIGHT_COORDINATE;
        float abs = c.absorption ? e.getAbsorptionAmount() : 0;
        int cells = heartCells(e.getMaxHealth(), abs);
        int left = -heartsWidth(cells) / 2;
        float hp = health(e), max = e.getMaxHealth();
        int total = Math.max(1, (int) Math.ceil(max / 2));
        for (int i = 0; i < cells; i++) {
            int cx = left + i % HEARTS_PER_ROW * 8, cy = i / HEARTS_PER_ROW * 10;
            quad(vc, m, light, cx, cy, U_EMPTY, 0.01f);
            boolean extra = i >= total;
            float rest = extra ? abs - (i - total) * 2 : hp - i * 2;
            if (rest >= 2) quad(vc, m, light, cx, cy, extra ? U_ABS_FULL : U_FULL, 0);
            else if (rest > 0) quad(vc, m, light, cx, cy, extra ? U_ABS_HALF : U_HALF, 0);
        }
    }

    /** 빈 하트는 살짝 뒤(z)에 둬서 채운 하트와 깊이가 겹치지 않게 한다. */
    private static void quad(VertexConsumer vc, Matrix4f m, int light, float x0, float y0, int u, float z) {
        float x1 = x0 + 9, y1 = y0 + 9;
        float u0 = u / 256f, u1 = (u + 9) / 256f, v1 = 9 / 256f;
        vc.vertex(m, x0, y0, z).color(255, 255, 255, 255).texture(u0, 0).light(light).next();
        vc.vertex(m, x0, y1, z).color(255, 255, 255, 255).texture(u0, v1).light(light).next();
        vc.vertex(m, x1, y1, z).color(255, 255, 255, 255).texture(u1, v1).light(light).next();
        vc.vertex(m, x1, y0, z).color(255, 255, 255, 255).texture(u1, 0).light(light).next();
    }

    // ── 표시 여부 ───────────────────────────────────────────

    private static boolean visible(Config c, LivingEntity e, EntityRenderDispatcher dispatcher) {
        if (!c.enabled || !e.isAlive()) return false;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden) return false;
        boolean self = e == mc.getCameraEntity();
        if (realPlayer(e)) {
            if (self && (!c.self || mc.options.getPerspective().isFirstPerson())) return false;
            if (!self && !c.players) return false;
        } else {
            // 플레이어 모양인데 접속자가 아니면 NPC, 그 밖은 생물. 갑옷 거치대는 서버가 글자 띄우기에 쓰므로 뺀다.
            if (e instanceof ArmorStandEntity) return false;
            if (!(e instanceof PlayerEntity ? c.npcs : c.entities)) return false;
        }
        if (invisibleToMe(e)) return false;
        if (e instanceof PlayerEntity p && !self && (p.isSneaky() || nameTagHiddenFromMe(p))) return false;
        if (c.hideFull && ratio(e) >= 1 && e.getAbsorptionAmount() <= 0) return false;
        double d = c.distance;
        return dispatcher.getSquaredDistanceToCamera(e) <= d * d && bodyVisible(e);
    }

    private static boolean invisibleToMe(Entity e) {
        PlayerEntity me = MinecraftClient.getInstance().player;
        return me == null || e.isInvisibleTo(me);
    }

    /**
     * 실제 접속한 플레이어인지. 서버의 NPC 플러그인은 NPC 를 접속자 목록에 잠깐 넣거나 탭에 보이지 않게 넣어 두므로,
     * 탭 목록에 보이는 사람만 접속자로 본다. Citizens 처럼 NPC 에 버전 2 UUID 를 쓰는 경우도 뺀다.
     */
    private static boolean realPlayer(Entity e) {
        if (!(e instanceof PlayerEntity)) return false;
        if (e.getUuid().version() == 2) return false;
        ClientPlayNetworkHandler net = MinecraftClient.getInstance().getNetworkHandler();
        if (net == null) return false;
        PlayerListEntry entry = net.getPlayerListEntry(e.getUuid());
        return entry != null && net.getListedPlayerListEntries().contains(entry);
    }

    /** 서버가 팀 설정(nametagVisibility)으로 이 플레이어의 이름표를 나에게 숨겼는지. 바닐라 이름표의 팀 판정과 같다. */
    private static boolean nameTagHiddenFromMe(PlayerEntity e) {
        PlayerEntity me = MinecraftClient.getInstance().player;
        AbstractTeam team = e.getScoreboardTeam();
        if (me == null || team == null) return false;
        AbstractTeam mine = me.getScoreboardTeam();
        return switch (team.getNameTagVisibilityRule()) {
            case ALWAYS -> false;
            case NEVER -> true;
            case HIDE_FOR_OTHER_TEAMS -> mine != null && !team.isEqual(mine);
            case HIDE_FOR_OWN_TEAM -> mine != null && team.isEqual(mine);
        };
    }

    /**
     * 카메라에서 이 상대의 몸(머리·가슴·발) 중 한 곳이라도 블록에 가리지 않고 보이는지.
     * VISUAL 모양으로 재므로 유리처럼 비쳐 보이는 블록은 가리는 것으로 치지 않는다. 틱마다 상대별로 한 번만 잰다.
     */
    private static boolean bodyVisible(LivingEntity e) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity cam = mc.getCameraEntity();
        if (mc.world == null || cam == null) return false;
        if (e == cam) return true;
        long tick = mc.world.getTime();
        if (tick != seenTick) {
            SEEN.clear();
            seenTick = tick;
        }
        return SEEN.computeIfAbsent(e.getId(), id -> {
            Vec3d from = mc.gameRenderer.getCamera().getPos();
            double[] heights = {e.getStandingEyeHeight(), e.getHeight() * 0.5, 0.1};
            for (double y : heights) {
                Vec3d to = new Vec3d(e.getX(), e.getY() + y, e.getZ());
                BlockHitResult hit = mc.world.raycast(new RaycastContext(from, to, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, cam));
                if (hit.getType() == HitResult.Type.MISS) return true;
            }
            return false;
        });
    }

    // ── 배치 ────────────────────────────────────────────────

    /** 머리 꼭대기에서 체력 표시 바닥까지(블록). 이름표가 있으면 그 위에 둔다. */
    private static float bottom(LivingEntity e, Config c) {
        boolean label = e instanceof PlayerEntity || e.shouldRenderName();
        float b = label ? LABEL_BASE + (scoreLine(e) ? LABEL_STEP : 0) + LINE + NAME_GAP : HEAD_GAP;
        return b + c.height / 100f;
    }

    /** 이 플레이어 이름표에 서버 점수 줄(이름 아래 표시)이 붙는지. 바닐라와 같이 10블록 안에서만 붙는다. */
    private static boolean scoreLine(Entity e) {
        if (!(e instanceof PlayerEntity p)) return false;
        if (p.getScoreboard().getObjectiveForSlot(Scoreboard.BELOW_NAME_DISPLAY_SLOT_ID) == null) return false;
        return MinecraftClient.getInstance().getEntityRenderDispatcher().getSquaredDistanceToCamera(e) < 100;
    }

    /** 하트 그림으로 그리는지. 칸이 너무 많은 큰 몹은 글자로 줄인다. */
    private static boolean icons(LivingEntity e, Config c) {
        return c.look == Look.HEARTS && heartCells(e.getMaxHealth(), c.absorption ? e.getAbsorptionAmount() : 0) <= MAX_HEART_CELLS;
    }

    /** 체력 표시가 차지하는 세로 칸 수(글자 크기 적용 전). */
    private static int pixels(LivingEntity e, Config c) {
        return icons(e, c) ? heartsHeight(heartCells(e.getMaxHealth(), c.absorption ? e.getAbsorptionAmount() : 0)) : 9;
    }

    // ── 값과 글자 ───────────────────────────────────────────

    private static float health(LivingEntity e) {
        return Math.max(0, e.getHealth());
    }

    private static float ratio(LivingEntity e) {
        float max = e.getMaxHealth();
        return max <= 0 ? 0 : MathHelper.clamp(health(e) / max, 0, 1);
    }

    /** 가득하면 초록, 절반이면 노랑, 바닥이면 빨강. */
    private static int gradient(float ratio) {
        int red = ratio > 0.5f ? (int) (255 * (1 - ratio) * 2) : 255;
        int green = ratio > 0.5f ? 255 : (int) (255 * ratio * 2);
        return red << 16 | green << 8 | 0x40;
    }

    /** 1.20.1 의 MutableText 에는 withColor(int) 가 없어 Style 로 색을 준다. */
    private static MutableText lit(String s, int color) {
        return Text.literal(s).styled(st -> st.withColor(color));
    }

    private static String number(float v) {
        return v == Math.floor(v) ? String.valueOf((int) v) : String.format(Locale.ROOT, "%.1f", v);
    }

    private static Text text(LivingEntity e, Look look, boolean absorption, int color) {
        float hp = health(e), max = e.getMaxHealth(), r = ratio(e);
        MutableText t = Text.empty();
        switch (look) {
            case NUMBER -> t.append(lit(HEART + " ", 0xFF5555)).append(lit(number(hp), color));
            case NUMBER_MAX -> t.append(lit(number(hp), color)).append(lit(" / " + number(max), 0xAAAAAA));
            case PERCENT -> t.append(lit(Math.round(r * 100) + "%", color));
            case HEARTS -> t.append(lit(HEART, 0xFF5555)).append(lit(" × " + number(hp / 2), color));
            case BAR -> {
                int filled = Math.round(r * 10);
                t.append(lit("|".repeat(filled), color));
                t.append(lit("|".repeat(10 - filled), 0x555555));
            }
        }
        float abs = e.getAbsorptionAmount();
        if (absorption && abs > 0) t.append(lit(" +" + number(abs), 0xFFCC33));
        return t;
    }

    /** 체력과 흡수 체력을 합한 하트 칸 수. */
    private static int heartCells(float max, float abs) {
        return Math.max(1, (int) Math.ceil(max / 2)) + (int) Math.ceil(Math.max(0, abs) / 2);
    }

    private static int heartsWidth(int cells) {
        return Math.min(cells, HEARTS_PER_ROW) * 8 + 1;
    }

    private static int heartsHeight(int cells) {
        return ((cells + HEARTS_PER_ROW - 1) / HEARTS_PER_ROW) * 10 - 1;
    }
}
