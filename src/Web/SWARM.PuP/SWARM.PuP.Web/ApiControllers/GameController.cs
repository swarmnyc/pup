using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.ApiControllers
{
    [Authorize]
    public class GameController : ApiController
    {
        private readonly IGameService _gameService;

        public GameController(IGameService gameService)
        {
            _gameService = gameService;
        }

        // GET: api/Game
        public IEnumerable<Game> Get()
        {
            return _gameService.All();
        }

        // GET: api/Game/5
        public Game Get(string id)
        {
            return _gameService.GetById(id);
        }

        // POST: api/Game
        public void Post(Game game)
        {
            _gameService.Add(game);
        }

        // PUT: api/Game/5
        public void Put(Game game)
        {
            _gameService.Update(game);
        }

        // DELETE: api/Game/5
        public void Delete(string id)
        {
            _gameService.Delete(id);
        }
    }
}
