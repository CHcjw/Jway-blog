# Jway Blog 内容维护教学

本文档告诉你：**在哪里添加/修改文章、标签、分类、作品、技能、统计、旧时光**。

## 0. 先确认你现在用哪种后端数据源

后端有两种运行方式：

1. `dev`（默认）
- 数据来自 Java 代码里的种子数据。
- 修改位置：`backend/src/main/java/com/jway/blog/bootstrap/DataInitializer.java`

2. `mysql`
- 数据来自 MySQL 表。
- 建表脚本：`backend/sql/mysql_schema.sql`
- 运行方式：

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

## 1. 添加文章（Post）

### 方式 A：dev 模式（最快）

修改文件：
- `backend/src/main/java/com/jway/blog/bootstrap/DataInitializer.java`

在 `seedPosts(...)` 中新增一条 `createPost(...)`。

同时注意：
- `category` 必须在 categories 里存在。
- `tagNames` 里的标签必须在 tags 里存在。

如果标签或分类不存在，先在同文件里新增：
- 分类：`run()` 方法中的 `List.of("学习笔记", ... )`
- 标签：`run()` 方法中的 `List.of("Java", ... )`

### 方式 B：mysql 模式

向这些表插入：
- `posts`
- `post_tags`
- `categories`（若分类不存在）
- `tags`（若标签不存在）

建议顺序：先 `categories/tags`，再 `posts`，最后 `post_tags`。

---

## 2. 添加标签与分类（Tag / Category）

### dev 模式

修改：
- `backend/src/main/java/com/jway/blog/bootstrap/DataInitializer.java`

位置：
- 分类列表：`for (String name : List.of(...))`（categories）
- 标签列表：`for (String name : List.of(...))`（tags）

### mysql 模式

执行 SQL：

```sql
INSERT INTO categories(name, create_time) VALUES ('新分类', NOW(6));
INSERT INTO tags(name, create_time) VALUES ('新标签', NOW(6));
```

---

## 3. 添加作品（Works）

### dev 模式

修改：
- `backend/src/main/java/com/jway/blog/bootstrap/DataInitializer.java`

在 `seedWorks()` 新增一个 `Work`。

字段说明：
- `imageUrl`: 必须是可直接访问的完整 URL
- `techs`: 用 `|` 分隔（例如 `Vue3|SpringBoot|Redis`）
- `points`: 用 `|` 分隔（例如 `难点A|难点B`）

### mysql 模式

插入表 `works`：

```sql
INSERT INTO works(title, image_url, description, period, team_size, techs, points)
VALUES ('项目名', 'https://...jpg', '项目描述', '3个月', '4人', 'Vue3|SpringBoot', '难点1|难点2');
```

---

## 4. 添加技能（Skills）

### dev 模式

修改：
- `backend/src/main/java/com/jway/blog/bootstrap/DataInitializer.java`

在 `seedSkills()` 里增加 `seedSkill(...)`。

`type` 说明：
- `1` = 前端
- `2` = 后端

示例：

```java
seedSkill("Go", "bi-cpu", "#00ADD8", 2);
```

### mysql 模式

```sql
INSERT INTO skills(name, icon, color, type)
VALUES ('Go', 'bi-cpu', '#00ADD8', 2);
```

---

## 5. 添加旧时光（Moments）

### dev 模式

修改：
- `backend/src/main/java/com/jway/blog/bootstrap/DataInitializer.java`

在 `seedMoments()` 增加一个 `Moment`。

字段建议：
- `timestamp`: `YYYY-MM-DD`
- `type`: `primary/success/warning/info`（Element Plus 风格）
- `size`: `large/default`

### mysql 模式

```sql
INSERT INTO moments(title, content, timestamp, type, size)
VALUES ('事件标题', '事件内容', '2026-03-06', 'primary', 'large');
```

---

## 6. 统计数据（Statistics）在哪里改

统计接口在：
- `backend/src/main/java/com/jway/blog/service/GlobalService.java`

当前规则（已改为真实统计）：
- `totalPosts`: 来自 `posts` 总数
- `totalViews`: 来自 `visit_events` 访问事件总数（前端路由切换会自动上报）
- `totalWords`: 所有文章 `content` 字数合计
- `totalVisitors`: 来自 `visitors` 去重访客数

如果你要重置统计：
- 清空 `visit_events` 和 `visitors` 表

---

## 7. 前端展示中的注意点

前端已改为请求后端 API，主要页面：
- 首页：`Front/src/views/Home.vue`
- 文章详情：`Front/src/views/PostDetail.vue`
- 分类：`Front/src/views/Categories.vue`
- 标签：`Front/src/views/Tags.vue`
- 作品：`Front/src/views/Works.vue`
- 技能：`Front/src/views/Skills.vue`
- 旧时光：`Front/src/views/OldTimes.vue`
- 统计：`Front/src/views/Statistics.vue`

如果你发现“数据改了但页面没变”，常见原因：
- 你在用 `mysql`，但改的是 `DataInitializer`
- 你在用 `dev`，但去改了 MySQL
- 后端没重启

---

## 8. 每次改完怎么生效

### 改了后端 Java（dev）

```bash
cd backend
mvn spring-boot:run
```

### 改了 MySQL 数据

- 不改 Java 时一般不用重启后端，刷新前端即可。
- 如果是首次建库/改表结构，建议重启一次后端。

### 前端启动

```bash
cd Front
npm run dev
```

打开浏览器：
- `http://localhost:5173`

---

## 9. 推荐维护流程（最稳）

1. 先用 `dev` 模式调整内容结构和展示效果（快）。
2. 确认没问题后，再同步到 MySQL（生产更可控）。
3. 对于文章类高频内容，优先走 MySQL，不要长期依赖种子代码。

---

## 10. 网页后台发布（已支持）

现在可以直接用后台页面发文（已做隐藏与加固）：

1. 后台前端路由通过环境变量配置：
- `VITE_ADMIN_PANEL_PATH`（例如 `/my-hidden-panel-9x`）
- 未配置时，前端不会注册后台路由
2. 登录需要三项：
- 入口口令（`ADMIN_ENTRY_KEY`）
- 管理员账号（`ADMIN_USERNAME`）
- 管理员密码哈希（推荐：`ADMIN_PASSWORD_HASH`，可选明文回退 `ADMIN_PASSWORD`）
3. 登录后进入后台文章页：`${VITE_ADMIN_PANEL_PATH}/posts`
- 左侧：文章列表（可编辑/删除）
- 右侧：发布/编辑表单（支持主标题/副标题、粗体、字号、引用、代码块、图片、目录模板、Markdown 正文、置顶）

相关文件：
- 后端接口：`backend/src/main/java/com/jway/blog/controller/AdminController.java`
- 鉴权：`backend/src/main/java/com/jway/blog/admin/AdminAuthInterceptor.java`
- 前端后台页：`Front/src/views/AdminLogin.vue`、`Front/src/views/AdminDashboard.vue`

后端管理接口路径已隐藏为：
- `${ADMIN_API_PREFIX}/*`（必须配置，示例 `/api/secret-cms-x9`）
