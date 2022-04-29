"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[87],{3905:function(e,t,n){n.d(t,{Zo:function(){return u},kt:function(){return m}});var r=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var l=r.createContext({}),c=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},u=function(e){var t=c(e.components);return r.createElement(l.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},f=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,l=e.parentName,u=s(e,["components","mdxType","originalType","parentName"]),f=c(n),m=o,d=f["".concat(l,".").concat(m)]||f[m]||p[m]||i;return n?r.createElement(d,a(a({ref:t},u),{},{components:n})):r.createElement(d,a({ref:t},u))}));function m(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,a=new Array(i);a[0]=f;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s.mdxType="string"==typeof e?e:o,a[1]=s;for(var c=2;c<i;c++)a[c]=n[c];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}f.displayName="MDXCreateElement"},1671:function(e,t,n){n.r(t),n.d(t,{assets:function(){return u},contentTitle:function(){return l},default:function(){return m},frontMatter:function(){return s},metadata:function(){return c},toc:function(){return p}});var r=n(7462),o=n(3366),i=(n(7294),n(3905)),a=["components"],s={id:"troubleshooting",title:"Troubleshooting"},l="Installation troubleshooting",c={unversionedId:"installation/troubleshooting",id:"installation/troubleshooting",title:"Troubleshooting",description:"Resolver issues",source:"@site/../comet-docs/target/mdoc/0-installation/troubleshooting.md",sourceDirName:"0-installation",slug:"/installation/troubleshooting",permalink:"/comet/docs/installation/troubleshooting",editUrl:"https://github.com/ulitol97/comet/tree/main/packages/create-docusaurus/templates/shared/../comet-docs/target/mdoc/0-installation/troubleshooting.md",tags:[],version:"current",frontMatter:{id:"troubleshooting",title:"Troubleshooting"},sidebar:"docsSidebar",previous:{title:"Instructions",permalink:"/comet/docs/installation/instructions"},next:{title:"Getting started",permalink:"/comet/docs/getting_started/example"}},u={},p=[{value:"Resolver issues",id:"resolver-issues",level:2}],f={toc:p};function m(e){var t=e.components,n=(0,o.Z)(e,a);return(0,i.kt)("wrapper",(0,r.Z)({},f,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"installation-troubleshooting"},"Installation troubleshooting"),(0,i.kt)("h2",{id:"resolver-issues"},"Resolver issues"),(0,i.kt)("p",null,"Comet binaries are currently hosted\nin ",(0,i.kt)("a",{parentName:"p",href:"https://search.maven.org/search?q=g:io.github.ulitol97"},"Sonatype"),"."),(0,i.kt)("p",null,"If ",(0,i.kt)("em",{parentName:"p"},"sbt")," is failing to fetch comet, add the following to your\nconfiguration in ",(0,i.kt)("inlineCode",{parentName:"p"},"build.sbt"),":"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},"// Add dependency resolvers for Sonatype\nresolvers ++= Seq(\n    Opts.resolver.sonatypeSnapshots,\n    Opts.resolver.sonatypeReleases\n  )\n```\ud83c\udffc\n")))}m.isMDXComponent=!0}}]);