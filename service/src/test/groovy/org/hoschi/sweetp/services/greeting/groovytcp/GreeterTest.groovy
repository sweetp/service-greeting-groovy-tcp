package org.hoschi.sweetp.services.greeting.groovytcp

import net.sf.json.JSONException
import org.gmock.WithGMock
import org.junit.Before
import org.junit.Test

@WithGMock
class GreeterTest {
	Greeter greeter

	@Before
	void setUp() {
		greeter = new Greeter()
	}

	@Test
	void sayHelloWorld() {
		assert 'Hello World!' == greeter.sayHello()
	}

	@Test
	void greetingWithNameGreetsGivenName() {
		assert "Hello John! I'm groovy over TCP" == greeter.greet([name: 'John'])
	}

	@Test
	void sendResponseOnAGivenRequest() {
		Socket socket = mock(Socket)
		socket.inputStream.returns(new ByteArrayInputStream(new byte[5]))
		socket.outputStream.returns(new ByteArrayOutputStream())

		BufferedReader reader = mock(BufferedReader, constructor(match {true}))
		reader.readLine().returns('test')

		PrintWriter writer = mock(PrintWriter, constructor(match {true}))
		writer.println(match {true})
		writer.flush()

		play {
			greeter.handleMessage(socket)
		}
	}

	@Test
	void send500ResponseIfMessageInterpretationDoNotWork() {
		Socket socket = mock(Socket)
		socket.inputStream.returns(new ByteArrayInputStream(new byte[5]))
		socket.outputStream.returns(new ByteArrayOutputStream())

		BufferedReader reader = mock(BufferedReader, constructor(match {true}))
		reader.readLine().returns('foo')

		PrintWriter writer = mock(PrintWriter, constructor(match {true}))
		writer.println(match {assert it.contains('Invalid JSON String'); true})
		writer.flush()

		play {
			greeter.handleMessage(socket)
		}
	}

	@Test
	void send500ResponseIfIllegalArgumentsArePassed() {
		Socket socket = mock(Socket)
		socket.inputStream.returns(new ByteArrayInputStream(new byte[5]))
		socket.outputStream.returns(new ByteArrayOutputStream())

		BufferedReader reader = mock(BufferedReader, constructor(match {true}))
		reader.readLine().returns('{"method":"greet"}')

		PrintWriter writer = mock(PrintWriter, constructor(match {true}))
		writer.println(match {assert it.contains('"status":500'); true})
		writer.flush()

		play {
			greeter.handleMessage(socket)
		}
	}

	@Test
	void send404ResponseIfMethodIsNotFound() {
		Socket socket = mock(Socket)
		socket.inputStream.returns(new ByteArrayInputStream(new byte[5]))
		socket.outputStream.returns(new ByteArrayOutputStream())

		BufferedReader reader = mock(BufferedReader, constructor(match {true}))
		reader.readLine().returns('{"method":"foo"}')

		PrintWriter writer = mock(PrintWriter, constructor(match {true}))
		writer.println(match {assert it.contains('"status":404'); true})
		writer.flush()

		play {
			greeter.handleMessage(socket)
		}
	}

	@Test
	void invokeMethodWhichIsDescribedInMessage() {
		assert 'Hello World!' == greeter.parseMessage('{"method":"sayHello"}').toString()
		assert "Hello John Doe! I'm groovy over TCP" == greeter.parseMessage('{"method":"greet", "params":{"name":"John Doe"}}').toString()
	}

	@Test(expected = JSONException)
	void throwsExceptionIfMessageIsNotJson() {
		greeter.parseMessage('foo')
	}

	@Test(expected = MissingMethodException)
	void throwsExceptionIfParserCanNotFindMethod() {
		greeter.parseMessage('{"method":"foo"}')
	}
}
