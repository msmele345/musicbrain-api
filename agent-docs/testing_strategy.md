### Test Strategy
- Tests run with Vitest in a jsdom environment. Place test files at `src/**/__tests__/*.spec.ts`.
- Write a failing test first then make it pass when writing implementation code. TDD style.
- Controllers should have @WebMvcTest integration slice tests
- For @WebMvcTests, Autowire the WebTestClient bean and use @MockitoBean to mock the service layer.
- Controllers should also have @ExtendWith(MockitoExtension.class) unit tests with a mocked web layer.
- For unit tests annotated with @ExtendWith(MockitoExtension.class), use Mockito's @Mock and @InjectMocks.
- In a future phase/iteration, I would like to add a performance testing gatling module with a groovy spec for each endpoint.

