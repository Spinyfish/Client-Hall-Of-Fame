package club.marsh.bloom.api.alt;

import java.awt.Color;
import java.io.IOException;
import java.net.Proxy;

import org.lwjgl.input.Keyboard;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public class GuiAltManager extends GuiScreen
{
	private boolean gameFullScreen = false, logInUsingEmailAndPass = false;
    private GuiTextField name, email, password;
    private String status = "";
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 5)
        {
        	if (logInUsingEmailAndPass)
    		{
    			logInUsingEmailAndPass = false;
    		}
    		
    		else
    		{
    			try
    			{
        			super.keyTyped((char) 0, 1);
    			}
    			
    			catch (Exception e)
    			{
    				;
    			}
    		}
        }
        
        else if (button.id == 3)
        {
            try
            {
            	if (!name.getText().isEmpty())
            	{
            		mc.session = new Session(name.getText(), "", "", "mojang");
                	status = EnumChatFormatting.GREEN + "Successfully logged in to " + name.getText() + ".";
            	}
            }
            
            catch (Exception e)
            {
                status = EnumChatFormatting.RED + "Could not login.";
            }
        }
        
        else if (button.id == 4)
        {
        	if (logInUsingEmailAndPass)
        	{
        		MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                MicrosoftAuthResult result = null;
                
                try
                {
                    status = EnumChatFormatting.GREEN + "Logging in...";
                    authenticator = new MicrosoftAuthenticator();
                    result = authenticator.loginWithCredentials(email.getText(), password.getText());
                    mc.session = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "mojang");
                    status = EnumChatFormatting.GREEN + "Successfully logged in to " + result.getProfile().getName();
                }
                
                catch (Exception e)
                {
                    status = EnumChatFormatting.RED + "Could not login.";
                }
        	}
        	
        	else
        	{
        		logInUsingEmailAndPass = true;
        	}
        }
        
        else if (button.id == 6)
        {
            TheAlteningAuthentication.theAltening().updateService(AlteningServiceType.THEALTENING);
            TheAlteningAuthentication.mojang().updateService(AlteningServiceType.THEALTENING);
            
            try
            {
                if (!this.name.getText().isEmpty())
                {
                    final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
                    auth.setUsername(this.name.getText());
                    auth.setPassword("trehfhdctgzhbnfftz");
                    auth.logIn();
                    mc.session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
                    this.status = EnumChatFormatting.GREEN + "Logged into: " + auth.getSelectedProfile().getName();
                }
            }

            catch (Exception e)
            {
                status = EnumChatFormatting.RED + "Could not login.";
            }
        }
        
        super.actionPerformed(button);
    }

    @Override
    public void initGui()
    {
    	int height = this.height / 4 + 24;
        name = new GuiTextField(1, mc.fontRendererObj, width / 2 - 100, 75, 200, 20);
        name.setMaxStringLength(9999);
        email = new GuiTextField(1, mc.fontRendererObj, width / 2 - 100, 75, 200, 20);
        email.setMaxStringLength(254);
        password = new GuiTextField(1, mc.fontRendererObj, width / 2 - 100, 100, 200, 20);
        password.setMaxStringLength(127);
        //MsaAuthenticationService msaAuthenticationService = new MsaAuthenticationService("");
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, height + 52, "Login (Cracked)"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, height + 75, "Login (Microsoft)"));
        this.buttonList.add(new GuiButton(6, this.width / 2 - 100, height + 98, "Login (Altening)"));
        this.buttonList.add(new GuiButton(5, this.width - 55, 5, 50, 20, "Cancel"));
        Keyboard.enableRepeatEvents(true);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        
        if (logInUsingEmailAndPass)
        {
        	this.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 128).getRGB());
        	this.drawCenteredString(this.fontRendererObj, "Alt Manager", this.width / 2, 20, -1);
        	this.drawCenteredString(this.fontRendererObj, status, this.width / 2, 40, -1);
            email.drawTextBox();
        	password.drawTextBox();
        	this.buttonList.get(1).drawButton(mc, mouseX, mouseY);
        	
        	if (email.getText().isEmpty())
            {
            	this.drawString(this.fontRendererObj, "Email", (width / 2) - mc.fontRendererObj.getStringWidth("Email")/2, 75+6, -7829368);
            }
        	
        	if (password.getText().isEmpty())
            {
            	this.drawString(this.fontRendererObj, "Password", (width / 2) - mc.fontRendererObj.getStringWidth("Password")/2, 100+6, -7829368);
            }
        }
        
        else
        {
        	name.drawTextBox();
        	this.drawCenteredString(this.fontRendererObj, "Alt Manager", this.width / 2, 20, -1);
            this.drawCenteredString(this.fontRendererObj, status, this.width / 2, 40, -1);
            
            if (name.getText().isEmpty())
            {
            	this.drawString(this.fontRendererObj, "Name", (width / 2) - mc.fontRendererObj.getStringWidth("Name")/2, 75+6, -7829368);
            }
            
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
    
    @Override
    protected void keyTyped(char character, int key) throws IOException
    {
    	if (key == 1)
    	{
    		if (logInUsingEmailAndPass)
    		{
    			logInUsingEmailAndPass = false;
    		}
    		
    		else
    		{
    			super.keyTyped(character, key);
    		}
    	}
    	
    	if (logInUsingEmailAndPass)
    	{
    		email.textboxKeyTyped(character, key);
    		password.textboxKeyTyped(character, key);
    	}
    	
    	else
    	{
            name.textboxKeyTyped(character, key);
    	}
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        
        if (logInUsingEmailAndPass)
        {
        	email.mouseClicked(mouseX, mouseY, mouseButton);
        	password.mouseClicked(mouseX, mouseY, mouseButton);
        }
        
        else
        {
        	name.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
    
    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public void updateScreen()
    {
    	if (logInUsingEmailAndPass)
    	{
            email.updateCursorCounter();
            password.updateCursorCounter();
    	}
    	
    	else
    	{
    		name.updateCursorCounter();
    	}
    }
}