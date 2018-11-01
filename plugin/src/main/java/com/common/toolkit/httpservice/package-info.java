/**
 * 此工具用来作为http服务暴露，直接去掉了控制器，通过特定url方式调用服务，
 * 1.local：直接使用本地spring容器中的服务
 * 2.remote：使用远程服务，调用方无需关心底层有谁实现，目前只支持dubbo（需要引入sdk）。
 */
package com.common.toolkit.httpservice;