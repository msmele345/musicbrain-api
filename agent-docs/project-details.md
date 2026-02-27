### Project Idea:
- Visual Dashboard of my current listening trends.
- Vue front end application. Spring boot backend
- The Core Concept - You pick or sync your favorite genres/artists, and the dashboard surfaces rich widgets around them — think recently played, top artists, genre breakdowns, trending songs in genres you love, related artist discovery, etc.
- UI should have Widgets containing news,trends,releases, and tour info for artists I'm currently listening to. These are just more ideas.
- UI could be a mood/energy map of my current listening
- Backend integrations with Last FM (connected to Scrobbler). Primary source will be Youtube music. 
- Azure Container Apps for deployment

### Additional Widget ideas for the Vue frontend:
* Genre breakdown (pie/donut chart)
* My Top artists this week/month
* "You might like" artist suggestions based on your current favorites
* New releases from artists you follow
* A mood/energy map of your listening


### Test Strategy
- Tests run with Vitest in a jsdom environment. Place test files at `src/**/__tests__/*.spec.ts`.
- Write unit tests in BDD format
- Write a failing test first then make it pass. TDD style.
- Cypress Tests for end-to-end testing.
