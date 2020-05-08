package columns;
import java.applet.*;
import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;


public class Columns extends Applet implements Runnable{
    static final int
    SL=25,
    Depth=15,
    Width=7,
    MaxLevel=7,
    TimeShift=250,
    FigToDrop=33,
    MinTimeShift=200,
    LeftBorder=2,
    TopBorder=2;
    
    
    Color colorsList[] = {Color.black,Color.cyan,Color.blue,Color.red,Color.green,
    Color.yellow,Color.pink,Color.magenta,Color.black};
    
    int Level, i, j, ii, k, ch;
    long Score, DScore, tc;
    Font fCourier;
    Figure Fig;
    int newFigure[][],oldFigure[][];
    boolean NoChanges = true, KeyPressed = false;
    Graphics _gr;
    
    Thread mainThread = null;
    
    
    void CheckNeighbours(int a, int b, int c, int d, int i, int j) {
        if ((newFigure[j][i]==newFigure[a][b]) && (newFigure[j][i]==newFigure[c][d])) {
            oldFigure[a][b] = 0;
            DrawBox(a,b,8);
            oldFigure[j][i] = 0;
            DrawBox(j,i,8);
            oldFigure[c][d] = 0;
            DrawBox(c,d,8);
            NoChanges = false;
            Score += (Level+1)*10;
            k++;
        };
    }
    void Delay(long t) {
        try {
            Thread.sleep(t);
        }
        catch (InterruptedException e) {};
    }
    void DrawBox(int x, int y, int c) {
        if (c==0) {
            _gr.setColor(Color.black);
            _gr.fillRect(LeftBorder+x*SL-SL, TopBorder+y*SL-SL, SL, SL);
            _gr.drawRect(LeftBorder+x*SL-SL, TopBorder+y*SL-SL, SL, SL);
        }
        else if (c==8) {
            _gr.setColor(Color.white);
            _gr.drawRect(LeftBorder+x*SL-SL+1, TopBorder+y*SL-SL+1, SL-2, SL-2);
            _gr.drawRect(LeftBorder+x*SL-SL+2, TopBorder+y*SL-SL+2, SL-4, SL-4);
            _gr.setColor(Color.black);
            _gr.fillRect(LeftBorder+x*SL-SL+3, TopBorder+y*SL-SL+3, SL-6, SL-6);
        }
        else {
            _gr.setColor(colorsList[c]);
            _gr.fillRect(LeftBorder+x*SL-SL, TopBorder+y*SL-SL, SL, SL);
            _gr.setColor(Color.black);
            _gr.drawRect(LeftBorder+x*SL-SL, TopBorder+y*SL-SL, SL, SL);
        }
        //		g.setColor (Color.black);
    }
    void DrawField(Graphics g) {
        for (int i=1; i<=Depth; i++) {
            for (int j=1; j<=Width; j++) {
                DrawBox(j,i,newFigure[j][i]);
            }
        }
    }
    void DrawFigure(Figure f) {
        DrawBox(f.x,f.y,f.c[1]);
        DrawBox(f.x,f.y+1,f.c[2]);
        DrawBox(f.x,f.y+2,f.c[3]);
    }
    void DropFigure(Figure f) {
        int zz = Depth;
        if (f.y < Depth-2) {
            while (newFigure[f.x][zz]>0) zz--;
            DScore = (((Level+1)*(Depth*2-f.y-zz) * 2) % 5) * 5;
            f.y = zz-2;
        }
    }
    boolean FullField() {
        for (int i=1; i<=Width; i++) {
            if (newFigure[i][3]>0)
                return true;
        }
        return false;
    }
    void HideFigure(Figure f) {
        DrawBox(f.x,f.y,0);
        DrawBox(f.x,f.y+1,0);
        DrawBox(f.x,f.y+2,0);
    }
    public void init() {
        newFigure = new int[Width+2][Depth+2];
        oldFigure = new int[Width+2][Depth+2];
        this.Fig = new Figure();
        Level = 0;
        Score = 0;
        _gr = getGraphics();
        addKeyListener(new KeyAdapter() {
        	@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					moveColumnLeft();
//					KeyPressed = true;
//					ch = Event.LEFT;
					break;
				case KeyEvent.VK_RIGHT:
					moveColumnRight();
//					KeyPressed = true;
//					ch = Event.RIGHT;
					break;
				case KeyEvent.VK_DOWN:
					moveCubeDown();
//					KeyPressed = true;
//					ch = Event.DOWN;
					break;
				case KeyEvent.VK_UP:
					moveCubeDown();
//					KeyPressed = true;
//					ch = Event.UP;
					break;
				
				case KeyEvent.VK_P:
					KeyPressed = true;
					ch = e.getKeyCode();
					break;
				
				case KeyEvent.VK_SPACE:
					KeyPressed = true;
					ch = e.getKeyCode();
					break;
					
				default:
					break;
				}
			}
        });
        //TODO - MOVE THREAD HERE
    }
    public boolean keyDown(Event e, int k) {
        KeyPressed = true;
        ch = e.key;
        return true;
    }
    public boolean lostFocus(Event e, Object w) {
        KeyPressed = true;
        ch = 'P';
        return true;
    }
    void PackField() {
        int n = Depth;
        for (int i=1; i<=Width; i++) {
            for (int j=n; j>0; j--) {
                if (oldFigure[i][j]>0) {
                    newFigure[i][n] = oldFigure[i][j];
                    n--;
                }
            };
            for (int j=n; j>0; j--) newFigure[i][j] = 0;
        }
    }
    public void paint(Graphics g) {
        //		ShowHelp(g);
        
        g.setColor(Color.black);
        
        ShowLevel(g);
        ShowScore(g);
        DrawField(g);
        DrawFigure(Fig);
        requestFocus();
    }
    void PasteFigure(Figure f) {
        newFigure[f.x][f.y] = f.c[1];
        newFigure[f.x][f.y+1] = f.c[2];
        newFigure[f.x][f.y+2] = f.c[3];
    }

    
    public void run() {
        for (int i=0; i<Width+1; i++){
            for (int j=0; j<Depth+1; j++) {
                newFigure[i][j] = 0;
                oldFigure[i][j] = 0;
            }
        }
        _gr.setColor(Color.black);
        requestFocus();
        do {
            tc = System.currentTimeMillis();
            DrawFigure(new Figure());
            while ((Fig.y<Depth-2) && (newFigure[Fig.x][Fig.y+3]==0)) {
                if ((int)(System.currentTimeMillis()-tc)>(MaxLevel-Level)*TimeShift+MinTimeShift) {
                    tc = System.currentTimeMillis();
                    HideFigure(Fig);
                    Fig.y++;
                    DrawFigure(Fig);
                }
                DScore = 0;
                do {
                    Delay(50);

                    if (KeyPressed) {
                        KeyPressed = false;
                        switch (ch) {
                            case Event.LEFT:
								moveColumnLeft();
                                break;
                            case Event.RIGHT:
								moveColumnRight();
                                break;
                            case Event.UP:
                            	switchCubeUp();
                                break;
                            case Event.DOWN:
								moveCubeDown();
								tc = 0;
                                break;
                            case ' ':
                            	dropColumn();
                                break;
                            case 'P':
                            case 'p':
                                while (!KeyPressed) {
                                    HideFigure(Fig);
                                    Delay(500);
                                    DrawFigure(Fig);
                                    Delay(500);
                                }
                                tc = System.currentTimeMillis();
                                break;
                            case '-':
                                if (Level > 0) Level--;
                                k=0;
                                ShowLevel(_gr);
                                break;
                            case '+':
                                if (Level < MaxLevel) Level++;
                                k=0;
                                ShowLevel(_gr);
                                break;
                        }
                    }
                } while ( (int)(System.currentTimeMillis()-tc) <= (MaxLevel-Level)*TimeShift+MinTimeShift );
            };
            PasteFigure(Fig);
            do {
                NoChanges = true;
                TestField();
                if (!NoChanges) {
                    Delay(500);
                    PackField();
                    DrawField(_gr);
                    Score += DScore;
                    ShowScore(_gr);
                    if (k>=FigToDrop) {
                        k = 0;
                        if (Level<MaxLevel) Level++;
                        ShowLevel(_gr);
                    }
                }
            } while (!NoChanges);
        }while (!FullField());
    }
	public void dropColumn() {
		HideFigure(Fig);
		DropFigure(Fig);
		DrawFigure(Fig);
//		tc = 0;
	}
	public void moveColumnRight() {
		if ((Fig.x<Width) && (newFigure[Fig.x+1][Fig.y+2]==0)) {
		    HideFigure(Fig);
		    Fig.x++;
		    DrawFigure(Fig);
		}
	}
	public void moveColumnLeft() {
		if ((Fig.x>1) && (newFigure[Fig.x-1][Fig.y+2]==0)) {
		    HideFigure(Fig);
		    Fig.x--;
		    DrawFigure(Fig);
		}
	}
	public void moveCubeDown() {
		int top = Fig.c[1];
		Fig.c[1] = Fig.c[3];
		Fig.c[3] = Fig.c[2];
		Fig.c[2] = top;
		DrawFigure(Fig);
	}
	public void switchCubeUp() {
		int top = Fig.c[1];
		Fig.c[1] = Fig.c[2];
		Fig.c[2] = Fig.c[3];
		Fig.c[3] = top;
		DrawFigure(Fig);
	}
    void ShowHelp(Graphics g) {
        g.setColor(Color.black);
        
        g.drawString(" Keys available:",200-LeftBorder,102);
        g.drawString("Roll Box Up:     ",200-LeftBorder,118);
        g.drawString("Roll Box Down:   ",200-LeftBorder,128);
        g.drawString("Figure Left:     ",200-LeftBorder,138);
        g.drawString("Figure Right:    ",200-LeftBorder,148);
        g.drawString("Level High/Low: +/-",200-LeftBorder,158);
        g.drawString("Drop Figure:   space",200-LeftBorder,168);
        g.drawString("Pause:           P",200-LeftBorder,180);
        g.drawString("Quit:     Esc or Q",200-LeftBorder,190);
    }
    void ShowLevel(Graphics g) {
        g.setColor(Color.black);
        g.clearRect(LeftBorder+100,390,100,20);
        g.drawString("Level: "+Level,LeftBorder+100,400);
    }
    void ShowScore(Graphics g) {
        g.setColor(Color.black);
        g.clearRect(LeftBorder,390,100,20);
        g.drawString("Score: "+Score,LeftBorder,400);
    }
    public void start() {
        _gr.setColor(Color.black);
        
        //		setBackground (new Color(180,180,180));
        
        if (mainThread == null) {
            mainThread = new Thread(this);
            mainThread.start();
        }
    }
    public void stop() {
        if (mainThread != null) {
            mainThread.stop();
            mainThread = null;
        }
    }
    void TestField() {
//        int i,j;
        for (int i=1; i<=Depth; i++) {
            for (int j=1; j<=Width; j++) {
                oldFigure[j][i] = newFigure [j][i];
            }
        }
        for (int i=1; i<=Depth; i++) {
            for (int j=1; j<=Width; j++) {
                if (newFigure[j][i]>0) {
                    CheckNeighbours(j,i-1,j,i+1,i,j);
                    CheckNeighbours(j-1,i,j+1,i,i,j);
                    CheckNeighbours(j-1,i-1,j+1,i+1,i,j);
                    CheckNeighbours(j+1,i-1,j-1,i+1,i,j);
                }
            }
        }
    }



	

}