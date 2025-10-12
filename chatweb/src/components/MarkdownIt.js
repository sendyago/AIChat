import MarkdownIt from 'markdown-it';
import hljs from 'highlight.js';

const md = new MarkdownIt({
  html: true, // 允许解析 HTML
  linkify: true, // 自动将 URL 转换为链接
  typographer: true, // 启用一些排版替换，如把 (c) 替换为 ©

  // 关键配置：指定代码高亮的函数
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        // 如果语言存在，则进行高亮处理
        return hljs.highlight(str, { language: lang, ignoreIllegals: true }).value;
      } catch (__) {}
    }
    // 否则，返回一个无高亮的 pre 标签
    return '<pre><code class="hljs">' + md.utils.escapeHtml(str) + '</code></pre>';
  }
});

export default md;
