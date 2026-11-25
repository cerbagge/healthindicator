# Health Indicator

[English](README.md) | **한국어**

> **공식 배포본: [1.2.0](https://github.com/cerbagge/healthindicator/releases/tag/v1.2.0)**  
> `healthindicator-mc1.20.1-1.2.0.jar` SHA-256: `50b8eaaba9f40dbad738953f30b8b43ae81c70b894b04e8ff20361effd5d166d`  
> 해시가 다른 파일은 공식 배포본이 아닙니다.

플레이어와 생물의 머리 위에 체력을 표시하는 Minecraft 1.20.1 Fabric 클라이언트 모드입니다.

**Health Indicator by cerbagge**

## 특징

- 체력 모양을 고를 수 있습니다: 숫자(`❤ 15`), 숫자와 최대(`15 / 20`), 하트 그림, 막대, 퍼센트.
- 위치는 이름표 바로 위(이름표가 없으면 머리 바로 위)로 고정되어 있으며 옮길 수 없습니다.
- 흡수 체력(노란 하트), 체력에 따른 색 변화(초록→빨강), 글자 크기, 보이는 거리를 설정할 수 있습니다.
- 플레이어, 생물(몹·동물), 서버의 플레이어 모양 NPC 를 각각 켜고 끌 수 있습니다.

## 안 보이는 상대의 위치를 알려 주지 않습니다 (ESP 없음)

이 모드는 화면에 보이는 상대의 체력만 보여 줍니다. 다음 상대는 체력을 표시하지 않습니다.

| 상황 | 처리 |
|---|---|
| 벽 너머의 상대 | 글자와 하트를 깊이 검사를 하는 층에 그려서 벽을 통과해 보이지 않습니다. |
| 몸이 블록에 가려진 상대 | 카메라에서 머리·가슴·발 중 한 곳도 보이지 않으면 그리지 않습니다. 그래서 머리 위에 뜬 체력만 벽 위로 보이는 일이 없습니다. |
| 투명한 상대(투명화 물약) | 그리지 않습니다. |
| 서버가 이름표를 숨긴 플레이어 | 팀 설정(`nametagVisibility`)으로 이름표가 숨겨지면 그리지 않습니다. |
| 웅크린 플레이어 | 그리지 않습니다. |
| F1 로 화면을 숨겼을 때 | 그리지 않습니다. |

좌표, 거리, 방향은 어디에도 표시하지 않습니다.

> 서버마다 규정이 다릅니다. 사용하는 서버의 규정은 직접 확인해 주십시오.

## 설치

1. Fabric Loader 0.16.10 이상과 Fabric API 를 설치합니다.
2. 이 저장소의 [Releases](https://github.com/cerbagge/healthindicator/releases)에서 받은 jar 를 `mods` 폴더에 넣습니다.

## 설정

처음 실행하면 `config/healthindicator.json` 이 만들어집니다. 게임을 끈 상태에서 고친 뒤 다시 실행하면 적용됩니다.

### 설정 방법

1. 게임을 한 번 실행했다가 끕니다. 이때 설정 파일이 만들어집니다.
2. 게임 폴더의 `config` 폴더를 엽니다.
   - 기본 런처: Windows 는 `%appdata%\.minecraft\config`, macOS 는 `~/Library/Application Support/minecraft/config`, Linux 는 `~/.minecraft/config`
   - 다른 런처(Prism, CurseForge, Modrinth 등): 해당 인스턴스 폴더 안의 `config`
3. `healthindicator.json` 을 메모장 같은 텍스트 편집기로 엽니다.
4. 바꾸고 싶은 값을 고치고 저장합니다. 따옴표(`"`)와 쉼표(`,`)는 그대로 두고 값만 바꿉니다.
5. 게임을 다시 실행합니다.

예시: 체력을 숫자로 보고, 몹 체력도 켜고, 글자를 조금 크게 하려면

```json
{
  "enabled": true,
  "look": "NUMBER",
  "scale": 120,
  "distance": 32,
  "players": true,
  "entities": true,
  "npcs": false,
  "self": false,
  "hideFull": false,
  "absorption": true,
  "gradient": true,
  "color": 16733525
}
```

`look` 값에 따른 모양:

| 값 | 모양 |
|---|---|
| `HEARTS` | 하트 그림(한 줄 10칸, 최대 2줄). 칸이 20을 넘는 큰 몹은 `❤ × 25` 글자로 표시 |
| `NUMBER` | `❤ 15` |
| `NUMBER_MAX` | `15 / 20` |
| `BAR` | 막대 10칸 |
| `PERCENT` | `75%` |

참고:

- 파일을 잘못 고쳐서 읽을 수 없으면 기본값으로 실행하고 파일을 기본값으로 다시 저장합니다. 고치기 전에 복사본을 만들어 두시면 좋습니다.
- `scale`(`50~200`)과 `distance`(`4~64`)는 범위를 벗어나면 가장 가까운 값으로 맞춰집니다.
- 이미 설정 파일이 있으면 그 파일의 값을 씁니다. 예전 버전에서 만든 파일은 `look` 이 `NUMBER` 로 남아 있으니, 하트 그림을 보려면 `HEARTS` 로 바꾸거나 파일을 지우고 다시 실행합니다.
- 켜기/끄기는 게임 안에서도 할 수 있습니다. 설정 → 조작 → 키 설정에서 "머리 위 체력 켜기/끄기" 에 키를 지정합니다(기본 키 없음).

### 항목

| 항목 | 기본값 | 설명 |
|---|---|---|
| `enabled` | `true` | 켜기/끄기. 조작 설정의 "머리 위 체력 켜기/끄기" 키로도 바꿀 수 있습니다(기본 키 없음). |
| `look` | `HEARTS` | `NUMBER`, `NUMBER_MAX`, `HEARTS`, `BAR`, `PERCENT` |
| `scale` | `100` | 글자 크기(%, `50~200`) |
| `distance` | `32` | 보이는 거리(블록, `4~64`) |
| `players` | `true` | 플레이어 체력 |
| `entities` | `false` | 생물(몹·동물) 체력 |
| `npcs` | `false` | 서버의 플레이어 모양 NPC 체력 |
| `self` | `false` | 3인칭에서 내 체력 |
| `hideFull` | `false` | 체력이 가득하면 숨기기 |
| `absorption` | `true` | 흡수 체력 함께 표시 |
| `gradient` | `true` | 체력에 따라 색 바꾸기 |
| `color` | `16733525` | `gradient` 가 꺼졌을 때 글자 색(RGB 정수, 16733525 = `#FF5555`) |

## 빌드

JDK 21 로 Gradle 을 실행합니다(모드 자체는 Java 17 대상입니다).

```
./gradlew build
```

## 라이선스

자체 라이선스입니다. 자세한 내용은 [LICENSE](LICENSE) 를 확인해 주십시오.

- **출처 표시**: 소개하거나 언급할 때 "Health Indicator by cerbagge" 와 이 저장소 주소를 함께 표시해야 합니다.
- **2차 배포 금지**: 원본이든 수정본이든 다시 배포할 수 없습니다(다른 사이트 업로드, 모드팩·런처 포함).
- **상업적 사용 금지**
- **코드 수정 금지**: 수정, 2차 저작물 제작, 코드 일부를 다른 프로젝트에 복사하는 것을 금지합니다.

소스 열람과, 이 저장소에서 공식 배포한 파일을 개인적·비상업적으로 사용하는 것만 허용합니다.
