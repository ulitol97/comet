"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[634],{3905:function(e,t,n){n.d(t,{Zo:function(){return u},kt:function(){return h}});var i=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);t&&(i=i.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,i)}return n}function r(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,i,a=function(e,t){if(null==e)return{};var n,i,a={},o=Object.keys(e);for(i=0;i<o.length;i++)n=o[i],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(i=0;i<o.length;i++)n=o[i],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var c=i.createContext({}),l=function(e){var t=i.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):r(r({},t),e)),n},u=function(e){var t=l(e.components);return i.createElement(c.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return i.createElement(i.Fragment,{},t)}},d=i.forwardRef((function(e,t){var n=e.components,a=e.mdxType,o=e.originalType,c=e.parentName,u=s(e,["components","mdxType","originalType","parentName"]),d=l(n),h=a,m=d["".concat(c,".").concat(h)]||d[h]||p[h]||o;return n?i.createElement(m,r(r({ref:t},u),{},{components:n})):i.createElement(m,r({ref:t},u))}));function h(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=n.length,r=new Array(o);r[0]=d;var s={};for(var c in t)hasOwnProperty.call(t,c)&&(s[c]=t[c]);s.originalType=e,s.mdxType="string"==typeof e?e:a,r[1]=s;for(var l=2;l<o;l++)r[l]=n[l];return i.createElement.apply(null,r)}return i.createElement.apply(null,n)}d.displayName="MDXCreateElement"},4064:function(e,t,n){n.r(t),n.d(t,{assets:function(){return u},contentTitle:function(){return c},default:function(){return h},frontMatter:function(){return s},metadata:function(){return l},toc:function(){return p}});var i=n(7462),a=n(3366),o=(n(7294),n(3905)),r=["components"],s={id:"info",title:"About this webpage"},c="About this webpage",l={unversionedId:"webpage_info/info",id:"webpage_info/info",title:"About this webpage",description:"The website for Comet is currently hosted in https://ulitol97.github.io/comet/. It is a React webpage",source:"@site/../comet-docs/target/mdoc/6-webpage_info/info.md",sourceDirName:"6-webpage_info",slug:"/webpage_info/info",permalink:"/comet/docs/webpage_info/info",editUrl:"https://github.com/ulitol97/comet/tree/main/packages/create-docusaurus/templates/shared/../comet-docs/target/mdoc/6-webpage_info/info.md",tags:[],version:"current",frontMatter:{id:"info",title:"About this webpage"},sidebar:"docsSidebar",previous:{title:"Logging",permalink:"/comet/docs/testing_auditing/logging"}},u={},p=[{value:"Website edition",id:"website-edition",level:2},{value:"Website creation guidelines",id:"website-creation-guidelines",level:2},{value:"Web pages",id:"web-pages",level:3},{value:"Web docs",id:"web-docs",level:3},{value:"Scaladoc",id:"scaladoc",level:3},{value:"Issues while creating the webpage",id:"issues-while-creating-the-webpage",level:2},{value:"Resources and further information",id:"resources-and-further-information",level:2}],d={toc:p};function h(e){var t=e.components,n=(0,a.Z)(e,r);return(0,o.kt)("wrapper",(0,i.Z)({},d,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"about-this-webpage"},"About this webpage"),(0,o.kt)("p",null,"The website for Comet is currently hosted in ",(0,o.kt)("a",{parentName:"p",href:"https://ulitol97.github.io/comet/"},"https://ulitol97.github.io/comet/"),". It is a ",(0,o.kt)("a",{parentName:"p",href:"https://reactjs.org/"},"React")," webpage\nautomatically generated by ",(0,o.kt)("a",{parentName:"p",href:"https://docusaurus.io/"},"Docusaurus"),"."),(0,o.kt)("p",null,"Pushes to the main branch trigger an automatic re-build and re-publish of the page with the latest changes (if any).\nThis is done with the\nfollowing ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/ulitol97/comet/blob/main/.github/workflows/publish_gh_pages.yml"},"GitHub action"),"."),(0,o.kt)("h2",{id:"website-edition"},"Website edition"),(0,o.kt)("p",null,"The website contents are located:"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},(0,o.kt)("u",null,"Inside the _[website](https://github.com/ulitol97/comet/tree/master/website) folder"),":_ Docusaurus configuration files, React pages, header/footer/sidebar contents, etc."),(0,o.kt)("li",{parentName:"ol"},(0,o.kt)("u",null,"Inside the _[docs](https://github.com/ulitol97/comet/tree/master/docs) folder"),":_ Markdown files, first processed by [mdoc](https://scalameta.org/mdoc/) and eventually by Docusaurus to create the [Web Docs](https://ulitol97.github.io/comet/docs/).")),(0,o.kt)("h2",{id:"website-creation-guidelines"},"Website creation guidelines"),(0,o.kt)("h3",{id:"web-pages"},"Web pages"),(0,o.kt)("p",null,"In order to create new pages, create a JS/TS file inside ",(0,o.kt)("em",{parentName:"p"},"website/src/pages")," for Docusaurus to be aware of its existence\nand assign them a URL based on their location inside the ",(0,o.kt)("em",{parentName:"p"},"pages")," folder."),(0,o.kt)("h3",{id:"web-docs"},"Web docs"),(0,o.kt)("p",null,"Create new pages using markdown syntax inside the ",(0,o.kt)("em",{parentName:"p"},"docs")," folder. These pages will be processed by ",(0,o.kt)("em",{parentName:"p"},"mdoc")," and assigned a\nURL inside ",(0,o.kt)("inlineCode",{parentName:"p"},"/docs")," by ",(0,o.kt)("em",{parentName:"p"},"docusaurus")," when running the task ",(0,o.kt)("inlineCode",{parentName:"p"},"docs/docusaurusCreateSite")," from SBT."),(0,o.kt)("h3",{id:"scaladoc"},"Scaladoc"),(0,o.kt)("p",null,"To publish the Scaladoc along with the website,\nthe Scaladoc was configured to be generated inside ",(0,o.kt)("em",{parentName:"p"},"website/static/scaladoc"),"."),(0,o.kt)("p",null,"From that endpoint, the Scaladoc static pages can be navigated as usual."),(0,o.kt)("h2",{id:"issues-while-creating-the-webpage"},"Issues while creating the webpage"),(0,o.kt)("p",null,"In order to use ",(0,o.kt)("em",{parentName:"p"},"mdoc")," in combination with ",(0,o.kt)("em",{parentName:"p"},"docusaurus"),", this ",(0,o.kt)("a",{parentName:"p",href:"https://scalameta.org/mdoc/docs/docusaurus.html"},"guide"),"\nwas followed. However, minor issues occurred:"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},"The ",(0,o.kt)("em",{parentName:"li"},"package.json")," had to be modified to include the script ",(0,o.kt)("inlineCode",{parentName:"li"},"publish-gh-pages"),"."),(0,o.kt)("li",{parentName:"ol"},"The ",(0,o.kt)("a",{parentName:"li",href:"https://github.com/ulitol97/comet/blob/master/website/docusaurus.config.js"},"Docusaurus config file")," had to be\nmodified to indicate what the location of the markdown files with the web docs is."),(0,o.kt)("li",{parentName:"ol"},"The ",(0,o.kt)("a",{parentName:"li",href:"https://github.com/ulitol97/comet/blob/master/website/sidebars.js"},"sidebar configuration file")," was modified\nto customize the sidebar that is used as navigation when browsing the web docs.")),(0,o.kt)("h2",{id:"resources-and-further-information"},"Resources and further information"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("a",{parentName:"li",href:"https://scalameta.org/mdoc/docs/docusaurus.html#publish-to-github-pages-from-ci"},"Publishing the website with GitHub actions"))))}h.isMDXComponent=!0}}]);