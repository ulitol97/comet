"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[151],{3905:function(e,t,n){n.d(t,{Zo:function(){return l},kt:function(){return d}});var r=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function g(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var c=r.createContext({}),s=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},l=function(e){var t=s(e.components);return r.createElement(c.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},p=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,c=e.parentName,l=g(e,["components","mdxType","originalType","parentName"]),p=s(n),d=o,f=p["".concat(c,".").concat(d)]||p[d]||u[d]||i;return n?r.createElement(f,a(a({ref:t},l),{},{components:n})):r.createElement(f,a({ref:t},l))}));function d(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,a=new Array(i);a[0]=p;var g={};for(var c in t)hasOwnProperty.call(t,c)&&(g[c]=t[c]);g.originalType=e,g.mdxType="string"==typeof e?e:o,a[1]=g;for(var s=2;s<i;s++)a[s]=n[s];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}p.displayName="MDXCreateElement"},4912:function(e,t,n){n.r(t),n.d(t,{assets:function(){return l},contentTitle:function(){return c},default:function(){return d},frontMatter:function(){return g},metadata:function(){return s},toc:function(){return u}});var r=n(7462),o=n(3366),i=(n(7294),n(3905)),a=["components"],g={id:"logging",title:"Logging"},c="Logging",s={unversionedId:"testing_auditing/logging",id:"testing_auditing/logging",title:"Logging",description:"Comet is a library meant to be used in third-party software projects,",source:"@site/../comet-docs/target/mdoc/5-testing_auditing/logging.md",sourceDirName:"5-testing_auditing",slug:"/testing_auditing/logging",permalink:"/comet/docs/testing_auditing/logging",editUrl:"https://github.com/ulitol97/comet/tree/main/packages/create-docusaurus/templates/shared/../comet-docs/target/mdoc/5-testing_auditing/logging.md",tags:[],version:"current",frontMatter:{id:"logging",title:"Logging"},sidebar:"docsSidebar",previous:{title:"Unit tests",permalink:"/comet/docs/testing_auditing/unit-tests"},next:{title:"About this webpage",permalink:"/comet/docs/webpage_info/info"}},l={},u=[],p={toc:u};function d(e){var t=e.components,n=(0,o.Z)(e,a);return(0,i.kt)("wrapper",(0,r.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"logging"},"Logging"),(0,i.kt)("p",null,"Comet is a library meant to be used in third-party software projects,\nthus the goal of providing a logging mechanism that does not interfere with\ncomet's parent project."),(0,i.kt)("p",null,"For this purpose, ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/lightbend/scala-logging"},"scala-logging"),"\nhas been used for several reasons:"),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},"It just provides a logging ",(0,i.kt)("strong",{parentName:"li"},"front-end"),", meaning the parent project is\nin charge of defining a logging mechanism and configuration,\nwhich will be honored by comet."),(0,i.kt)("li",{parentName:"ol"},"It is a Scala wrapper for the ",(0,i.kt)("a",{parentName:"li",href:"https://www.slf4j.org/"},"SLF4J"),", a mature and\nreliable Java logging framework."),(0,i.kt)("li",{parentName:"ol"},"Provides several macros and utilities to reduce the verbosity of the code\nin charge logging messages. Take for example:")),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'// 1. Extend LazyLogging\nobject Main extends IOApp with LazyLogging {\n  override def run(args: List[String]): IO[ExitCode] = {\n    // 2. Log anything from anywhere in the class\n    logger.debug("Launching comet")\n    IO.pure(ExitCode.Success)\n  }\n}\n')))}d.isMDXComponent=!0}}]);