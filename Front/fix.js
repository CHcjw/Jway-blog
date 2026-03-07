const fs = require('fs');
let f = fs.readFileSync('src/views/Home.vue', 'utf8');
f = f.replace('<span class="meta-item top-label"><i class="bi bi-pin-angle-fill"></i> 置顶</span>', '<span v-if="post.isTop" class="meta-item top-label"><i class="bi bi-pin-angle-fill"></i> 置顶</span>');
f = f.replace('<span class="divider">|</span>', '<span v-if="post.isTop" class="divider">|</span>');
f = f.replace('<el-tag v-for="tag in postStore.tagNames" :key="tag"', '<el-tag v-for="tag in postStore.tagNames.slice(0, 15)" :key="tag"');
fs.writeFileSync('src/views/Home.vue', f);
