using Mango;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services
{
    public class GameService : MangoService<Game>, IGameService
    {
        private readonly IChatService _chatService;

        public GameService(IChatService chatService)
        {
            _chatService = chatService;
        }

        public override Game Add(Game game)
        {
            game.ChatRoomId = _chatService.CreateRoom(ChatRoomType.Public, "Game:" + game.Name);
            return base.Add(game);
        }
    }
}