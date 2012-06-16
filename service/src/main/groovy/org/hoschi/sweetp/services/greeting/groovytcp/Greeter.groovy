package org.hoschi.sweetp.services.greeting.groovytcp

import groovy.util.logging.Log4j
import org.hoschi.sweetp.services.base.tcp.groovy.TcpService

/**
 * Greeter class to test a service written in groovy but with tcp to
 * communicate.
 */
@Log4j
class Greeter extends TcpService {

	static void main(String[] args) throws Exception {
		def port = System.getenv('PORT')
		assert port, "Environment variable 'PORT' is not set, bye"
		assert port.isInteger(), "Environment variable 'PORT' is not an integer, bye"

		TcpService own = new Greeter()
		own.connect('localhost', new Integer(port))
		own.listen()
	}

	/**
	 * Get config of service.
	 *
	 * @param params not used
	 * @return config
	 */
	@Override
	List getConfig(Map params) {
		[
				['/tests/service/groovy-tcp/sayhello': [
						method: 'sayHello',
				]],
				['/tests/service/groovy-tcp/greet': [
						method: 'greet',
						'params': [
								'name': 'one'
						]
				]]
		]
	}

	/**
	 * Say hello world.
	 *
	 * @param params not used
	 * @return hello string
	 */
	@SuppressWarnings('UnusedMethodParameter')
	String sayHello(Map params) {
		'Hello World!'
	}

	/**
	 * Greet someone.
	 *
	 * @param params with name to greet
	 * @return greeting string
	 */
	String greet(Map params) {
		'Hello ' + params.name + "! I'm groovy over TCP"
	}

}
