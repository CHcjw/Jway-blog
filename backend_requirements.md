# Jway's Blog - 后端开发需求分析文档

你好，ChatGPT 5.3（Codex 模型）。这份文档是基于当前已完成的前端项目（Vue 3 + Vite + Pinia）总结出的后端开发需求指南。目前前端的所有数据均在组件或 Store 内部被静态 Mock，你的任务是设计和开发相应的后端服务（推荐使用 **Java Spring Boot** 架构，因为本博客极具 Java 技术栈偏向），提供 RESTful API 以替换前端现有的 Mock 数据。

以下是全面而详细的需求拆解：

## 一、 项目架构与技术栈建议
前端的 `package.json` 中已经引入了 `axios` 用于请求，你需要提供标准的 JSON 格式响应。
推荐的响应体结构（Result Wrapper）：
```json
{
  "code": 200,          // 状态码（200 成功）
  "message": "success", // 提示信息
  "data": { ... }       // 核心数据载体
}
```

**后端推荐技术选型：**
* 框架：Spring Boot 3.x
* 语言：Java 17 或以上
* 数据库：MySQL 8.x
* 缓存与中间件：Redis（可选，用于文章阅读量、统计数据等）
* 接口文档：Swagger / SpringDoc (用于与前端联调对接)

## 二、 核心数据模型分析 (Database Design)

通过分析前端视图，我们需要以下核心实体表（至少）：

### 1. 文章表 (`post`)
用于首页列表、文章详情、分类过滤等。
* `id` (Long, 主键)
* `title` (String, 标题)
* `summary` (String, 摘要)
* `content` (Text/LongText, Markdown 正文内容)
* `cover_url` (String, 封面图 URL)
* `category_id` / `category_name` (关联分类)
* `publish_date` (Date/Datetime, 发布时间)
* `views` (Integer, 浏览量 - 可选扩充)
* `is_top` (Boolean, 是否置顶 - 首页 UI 包含置顶标签)

### 2. 标签与分类 (`tag`, `category`, `post_tag_relation`)
前端存在 `/tags` 和 `/categories` 视图。需要支持：
* 分类表：`id`, `name`, `create_time`
* 标签表：`id`, `name`, `create_time`
* 文章-标签关联表（多对多关系）

### 3. 作品集 / 项目经历 (`work`)
对应 `Works.vue` 界面。
* `id` (Long)
* `title` (String, 项目名称)
* `image_url` (String, 项目横幅封面)
* `description` (String, 项目简述)
* `period` (String, 开发周期，如"5个月")
* `team_size` (String, 团队规模)
* `techs` (JSON/String, 技术栈数组，如 "[\"Vue3\", \"SpringBoot\"]")
* `points` (JSON/String, 核心攻坚难点数组)

### 4. 技能树 (`skill`)
对应 `Skills.vue` 界面。
* `id` (Long)
* `name` (String, 技能名称)
* `icon` (String, 对应的 bootstrap-icons 类名，如 "bi-cup-hot")
* `color` (String, 主题色十六进制，如 "#ed8b00")
* `type` (Integer/Enum，区分 1=前端，2=后端)

### 5. 旧时光 / 时光轴 (`moment` / `timeline`)
对应 `OldTimes.vue` 界面。
* `id` (Long)
* `title` (String, 简短标题)
* `content` (String, 详细描述)
* `timestamp` (Date, 发生时间)
* `type` (String, 如 "primary", "success"，用于前端 Element UI 时间轴着色)
* `size` (String, 如 "large"，用于控制节点大小)

---

## 三、 API 接口需求定义 (RESTful API Design)

前端通过 `axios` 异步获取数据，请完成以下接口的设计与开发。

### 模块 A：文章（Post API）
1. **获取文章分页列表**
   * Endpoint: `GET /api/posts`
   * Params: `page` (当前页码), `size` (每页数量，前端目前为 9)
   * Response: 包含 `total` 和 `list`（仅包含 `id`, `title`, `cover`, `summary`, `date`, `category`, `tags` 等轻量字段，**不包含正文**）。
2. **获取单篇文章详情**
   * Endpoint: `GET /api/posts/{id}`
   * Response: 返回带有完整 `content` (Markdown格式) 的文章对象。
3. **获取最新文章推荐**
   * Endpoint: `GET /api/posts/latest`
   * Params: `limit` (前端边栏显示 5 篇)
4. **获取相关推荐文章**
   * Endpoint: `GET /api/posts/related/{id}`
   * 逻辑：根据当前文章的标签或分类，返回 4 篇相关文章对象（用于 `PostDetail.vue` 底部）。

### 模块 B：分类与标签（Category & Tag API）
1. **获取全部分类统计**
   * Endpoint: `GET /api/categories`
   * Response: 字符串数组 `['学习笔记', '技术分享', ...]`（进阶：返回对象数组包含每个分类的文章计数，以支持分类页面直接展示统计）。
2. **获取全部标签统计**
   * Endpoint: `GET /api/tags`
   * Response: 返回全站所有标签的列表（用于全局搜索和侧边栏标签云）。
3. **根据条件查询文章列表**
   * Endpoint: `GET /api/posts/search`
   * Params: 包含 `keyword` (全文搜索), `category` (分类归档), `tag` (标签过滤)。(复用 `GET /api/posts` 或单独拆分)。

### 模块 C：作品与技能（Works & Skills API）
1. **获取作品集列表**
   * Endpoint: `GET /api/works`
   * Response: 返回作品对象数组，包含其技术栈、周期和核心攻坚点。
2. **获取核心技能列表**
   * Endpoint: `GET /api/skills`
   * Response: 返回前端/后端技能分组的数据结构，包含图标与主题色信息。

### 模块 D：全局与状态（Global & Moments API）
1. **获取时光轴（旧时光）列表**
   * Endpoint: `GET /api/moments`
   * Response: 根据时间倒序排列的站点大事件或个人动态。
2. **获取博客运行统计数据**
   * Endpoint: `GET /api/statistics`
   * Response: 返回 `totalPosts`, `totalWords` (总字数), `totalViews` (总访问量), `totalVisitors` (访客数) 等聚合数据，驱动 `Statistics.vue` 界面渲染。

## 四、 其他开发重点细节
1. **前端图片路径**：当前前端使用 `getOssUrl('dir', 'file')` Mock 了很多阿里云 OSS 图片。后端返回图片 URL 时，请确保提供可以直接访问的完整 HTTP 地址。
2. **分页逻辑落地**：前端 `Home.vue` 已预留了 `<el-pagination>`，当前是用 slice 对数组假分页。你要提供稳健的 Page 对象进行对接。
3. **Markdown 渲染**：后端仅负责存储纯文本的 Markdown 内容，并将其原样返回即可，前端已使用 `markdown-it` 配合 Shiki 完成了语法高亮与渲染。
4. **全局搜索 (Spotlight Search)**：前端的 `App.vue` 中的高仿 Spotlight 高级搜索需要模糊匹配文章标题和类别。考虑在后端实现基于 SQL `LIKE` 或更为高级的全文检索支持。

祝编码愉快，请直接根据本文档构建你的项目结构、Domain实体、Mapper层、Service层与 Controller！
